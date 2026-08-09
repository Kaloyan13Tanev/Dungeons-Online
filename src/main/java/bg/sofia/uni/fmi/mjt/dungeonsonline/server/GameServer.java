package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.PlayerConnection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngine;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.RequestHandler;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.IdPool;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ResponseMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.HandshakeResponse;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer {

    private static final int SERVER_PORT = 4444;
    private static final int NO_PLAYER_ID = 0;
    private static final String REJECTION_MESSAGE = "Server is full. Try again later.";
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

    private final ResponseMapper mapper = new ResponseMapper();
    private final IdPool pool;
    private final ConnectionRegistry registry;
    private final RequestHandler handler;
    private final GameEngine engine;
    private final ServerSocket serverSocket;
    private final AtomicBoolean open = new AtomicBoolean();

    public GameServer(IdPool pool, ConnectionRegistry registry, RequestHandler handler, GameEngine engine)
        throws IOException {
        this.pool = pool;
        this.registry = registry;
        this.handler = handler;
        this.engine = engine;
        this.serverSocket = new ServerSocket(SERVER_PORT);
    }

    public void run() {
        open.set(true);
        LOGGER.log(Level.CONFIG, "Server listening on port {0}, capacity {1} players.",
            new Object[] {SERVER_PORT, pool.capacity()});

        try (serverSocket;
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            while (open.get()) {
                Optional<Socket> socket = awaitConnection();
                if (socket.isEmpty()) {
                    break;
                }

                admit(socket.get(), executor);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unrecoverable error in the accept loop. The server is shutting down.", e);
            throw new RuntimeException(e);
        } finally {
            open.set(false);
        }
    }

    private Optional<Socket> awaitConnection() throws IOException {
        try {
            return Optional.of(serverSocket.accept());
        } catch (IOException e) {
            if (open.get()) {
                throw e;
            }

            LOGGER.log(Level.INFO, "Server socket closed, shutting down.");

            return Optional.empty();
        }
    }

    private void admit(Socket socket, ExecutorService executor) {
        Optional<Integer> id = pool.acquire();
        if (id.isEmpty()) {
            reject(socket);
            return;
        }

        int playerId = id.get();
        try {
            executor.execute(() -> accept(playerId, socket));
        } catch (RejectedExecutionException e) {
            LOGGER.log(Level.WARNING, "Could not start a session for player " + playerId + ".", e);
            pool.release(playerId);
            closeQuietly(socket);
        }
    }

    private void accept(int playerId, Socket socket) {
        try (socket) {
            PlayerConnection connection = new PlayerConnection(playerId, socket);
            registry.register(connection);
            registry.sendTo(playerId, new HandshakeResponse(true, playerId, null, engine.terrain()));

            handler.distribute(engine.join(playerId));
            LOGGER.log(Level.INFO, "Player {0} connected.", playerId);

            connection.listen(message -> handler.handle(playerId, message));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Connection failed for player " + playerId + ".", e);
        } finally {
            registry.unregister(playerId);
            handler.distribute(engine.leave(playerId));
            pool.release(playerId);
            LOGGER.log(Level.INFO, "Player {0} disconnected.", playerId);
        }
    }

    private void reject(Socket socket) {
        try (socket;
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            writer.write(mapper.serialize(new HandshakeResponse(false, NO_PLAYER_ID, REJECTION_MESSAGE, null)));
            writer.newLine();
            writer.flush();
            LOGGER.log(Level.INFO, "Rejected a connection from {0}: the server is full.",
                socket.getRemoteSocketAddress());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING,
                "Failed to send the rejection notice to " + socket.getRemoteSocketAddress() + ".", e);
        }
    }

    private void closeQuietly(Closeable socket) {
        if (socket == null) {
            return;
        }

        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to close a resource during teardown.", e);
        }
    }

}
