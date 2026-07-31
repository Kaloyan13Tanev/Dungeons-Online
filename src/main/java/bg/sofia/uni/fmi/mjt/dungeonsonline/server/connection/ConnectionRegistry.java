package bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionRegistry {

    private final Map<Integer, PlayerConnection> connections = new ConcurrentHashMap<>();

    public PlayerConnection register(int playerId, Socket socket) throws IOException {
        PlayerConnection connection = new PlayerConnection(playerId, socket);
        connections.put(playerId, connection);

        return connection;
    }

    public void unregister(int playerId) {
        PlayerConnection connection = connections.remove(playerId);
        if (connection != null) {
            connection.close();
        }
    }

    public void sendTo(int playerId, String message) {
        PlayerConnection connection = connections.get(playerId);
        if (connection != null) {
            connection.send(message);
        }
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
