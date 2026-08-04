package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.PlayerConnection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.pool.IdPool;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.RequestMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.AttackRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.CastRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.DropRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.GiveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.PickUpRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.QuitRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

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

    private static final int SERVER_PORT = 8080;
    private static final String REJECTION_MESSAGE = "Server is full. Try again later.";
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

    private final IdPool pool;
    private final ConnectionRegistry registry;
    private final RequestMapper mapper = new RequestMapper();
    private final ServerSocket serverSocket;
    private final AtomicBoolean open = new AtomicBoolean();

    public GameServer(IdPool pool, ConnectionRegistry registry) throws IOException {
        this.pool = pool;
        this.registry = registry;
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
                    executor.submit(() -> accept(playerId, socket));
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
            connection.send("ACCEPTED " + playerId);
            LOGGER.log(Level.INFO, "Player {0} connected.", playerId);

            connection.listen(message -> handle(playerId, message));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Connection failed for player " + playerId + ".", e);
        } finally {
            registry.unregister(playerId);
            registry.sendToAll("Player " + playerId + " left the game.");
            pool.release(playerId);
            LOGGER.log(Level.INFO, "Player {0} disconnected.", playerId);
        }
    }

    private void handle(int playerId, String message) {
        Request request;
        try {
            request = mapper.deserialize(message);
        } catch (InvalidRequestException e) {
            registry.sendTo(playerId, "That command could not be read by the server.");
            LOGGER.log(Level.WARNING, "Failed to deserialize a request from player " + playerId + ".", e);
            return;
        }

        LOGGER.log(Level.INFO, "Player {0} sent {1}.", new Object[] {playerId, request});
        switch (request) {
            case MoveRequest(Direction direction) ->
                registry.sendToAll("Player " + playerId + " moved " + direction);
            case QuitRequest ignored -> registry.unregister(playerId);
            case PickUpRequest ignored -> registry.sendTo(playerId, "Picking up is not implemented yet.");
            case GiveRequest ignored -> registry.sendTo(playerId, "Giving items is not implemented yet.");
            case AttackRequest ignored -> registry.sendTo(playerId, "Attacking is not implemented yet.");
            case CastRequest ignored -> registry.sendTo(playerId, "Casting is not implemented yet.");
            case DropRequest ignored -> registry.sendTo(playerId, "Dropping is not implemented yet.");
        }
    }

    private void reject(Socket socket) {
        try (socket;
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            writer.write(REJECTION_MESSAGE);
            writer.flush();
            LOGGER.log(Level.INFO, "Rejected a connection from {0}: the server is full.",
                socket.getRemoteSocketAddress());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING,
                "Failed to send the rejection notice to " + socket.getRemoteSocketAddress() + ".", e);
            throw new RuntimeException(e);
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
