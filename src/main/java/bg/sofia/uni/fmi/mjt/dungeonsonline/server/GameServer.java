package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.PlayerConnection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngine;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.RequestHandler;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.IdPool;

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
    private static final String REJECTION_MESSAGE = "Server is full. Try again later.";
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

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

        ServerSocket server = serverSocket;
        try (server;
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            while (open.get()) {
                Socket socket;
                try {
                    socket = server.accept();
                } catch (IOException e) {
                    if (!open.get()) {
                        LOGGER.log(Level.INFO, "Server socket closed, shutting down.");
                        break;
                    }
                    throw e;
                }

                Optional<Integer> id = pool.acquire();
                if (id.isEmpty()) {
                    reject(socket);
                    continue;
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
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unrecoverable error in the accept loop. The server is shutting down.", e);
            throw new RuntimeException(e);
        } finally {
            open.set(false);
        }
    }

    private void accept(int playerId, Socket socket) {
        try (socket) {
            PlayerConnection connection = new PlayerConnection(playerId, socket);
            registry.register(connection);
            engine.join(playerId);
            connection.send("ACCEPTED " + playerId);
            LOGGER.log(Level.INFO, "Player {0} connected.", playerId);

            connection.listen(message -> handler.handle(playerId, message));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Connection failed for player " + playerId + ".", e);
        } finally {
            engine.leave(playerId);
            registry.unregister(playerId);
            registry.sendToAll("Player " + playerId + " left the game.");
            pool.release(playerId);
            LOGGER.log(Level.INFO, "Player {0} disconnected.", playerId);
        }
    }

    private void reject(Socket socket) {
        try (socket;
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            writer.write(REJECTION_MESSAGE);
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
