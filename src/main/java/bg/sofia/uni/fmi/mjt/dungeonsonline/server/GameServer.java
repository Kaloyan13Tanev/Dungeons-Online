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

public class GameServer {

    private static final int SERVER_PORT = 8080;
    private static final String REJECTION_MESSAGE = "Server is full. Try again later.";

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

        ServerSocket server = serverSocket;
        try (server;
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            while (open.get()) {
                Socket socket;
                try {
                    socket = server.accept();
                } catch (IOException e) {
                    if (!open.get()) {
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
                    pool.release(playerId);
                    closeQuietly(socket);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            open.set(false);
        }
    }

    private void accept(int playerId, Socket socket) {
        try (socket) {
            PlayerConnection connection = registry.register(playerId, socket);
            connection.send("ACCEPTED " + playerId);

            connection.listen(message -> handle(playerId, message));
        } catch (IOException e) {
        } finally {
            registry.unregister(playerId);
            registry.sendToAll("Player " + playerId + " left the game.");
            pool.release(playerId);
        }
    }

    private void handle(int playerId, String message) {
        Request request;
        try {
            request = mapper.deserialize(message);
        } catch (InvalidRequestException e) {
            registry.sendTo(playerId, "That command could not be read by the server.");
            return;
        }

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeQuietly(Closeable resource) {
        if (resource == null) {
            return;
        }

        try {
            resource.close();
        } catch (IOException e) {
        }
    }
}
