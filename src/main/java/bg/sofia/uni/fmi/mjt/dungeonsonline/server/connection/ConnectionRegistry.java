package bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionRegistry {

    private static final Logger LOGGER = Logger.getLogger(ConnectionRegistry.class.getName());

    private final Map<Integer, PlayerConnection> connections = new ConcurrentHashMap<>();

    public PlayerConnection register(int playerId, Socket socket) throws IOException {
        PlayerConnection connection = new PlayerConnection(playerId, socket);

        PlayerConnection existing = connections.putIfAbsent(playerId, connection);
        if (existing != null) {
            LOGGER.log(Level.SEVERE, "Id {0} was handed out while it was still registered.", playerId);
            connection.close();

            throw new IllegalStateException("Player " + playerId + " is already registered");
        }

        LOGGER.log(Level.FINE, "Registered player {0}. Connections open: {1}.",
            new Object[] {playerId, connections.size()});

        return connection;
    }

    public void unregister(int playerId) {
        PlayerConnection connection = connections.remove(playerId);
        if (connection == null) {
            LOGGER.log(Level.FINE, "Nothing to unregister for player {0}.", playerId);
            return;
        }

        connection.close();
        LOGGER.log(Level.FINE, "Unregistered player {0}. Connections open: {1}.",
            new Object[] {playerId, connections.size()});
    }

    public void sendTo(int playerId, String message) {
        PlayerConnection connection = connections.get(playerId);
        if (connection == null) {
            LOGGER.log(Level.FINE, "Dropped a message for player {0}, who is no longer connected.", playerId);
            return;
        }

        connection.send(message);
    }

    public void sendTo(Collection<Integer> playerIds, String message) {
        for (int playerId : playerIds) {
            sendTo(playerId, message);
        }
    }

    public void sendToAll(String message) {
        for (PlayerConnection connection : connections.values()) {
            connection.send(message);
        }
    }
}
