package bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ResponseMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.Response;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionRegistry {

    private static final Logger LOGGER = Logger.getLogger(ConnectionRegistry.class.getName());

    private final ResponseMapper mapper;
    private final Map<Integer, PlayerConnection> connections;

    public ConnectionRegistry() {
        this(new ConcurrentHashMap<>(), new ResponseMapper());
    }

    ConnectionRegistry(Map<Integer, PlayerConnection> connections) {
        this(connections, new ResponseMapper());
    }

    ConnectionRegistry(Map<Integer, PlayerConnection> connections, ResponseMapper mapper) {
        this.connections = connections;
        this.mapper = mapper;
    }

    public void register(PlayerConnection connection) {
        int playerId = connection.playerId();

        PlayerConnection existing = connections.putIfAbsent(playerId, connection);
        if (existing != null) {
            LOGGER.log(Level.SEVERE, "Id {0} was handed out while it was still registered.", playerId);
            connection.close();

            throw new IllegalStateException("Player " + playerId + " is already registered");
        }

        LOGGER.log(Level.FINE, "Registered player {0}. Connections open: {1}.",
            new Object[] {playerId, connections.size()});
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

    public void sendTo(int playerId, Response response) {
        send(playerId, mapper.serialize(response));
    }

    public void sendTo(Collection<Integer> playerIds, Response response) {
        String message = mapper.serialize(response);

        for (int playerId : playerIds) {
            send(playerId, message);
        }
    }

    private void send(int playerId, String message) {
        PlayerConnection connection = connections.get(playerId);
        if (connection == null) {
            LOGGER.log(Level.FINE, "Dropped a message for player {0}, who is no longer connected.", playerId);
            return;
        }

        connection.send(message);
    }

}
