package bg.sofia.uni.fmi.mjt.dungeonsonline.server.player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerIdPool {

    private static final int MAX_PLAYERS = 3;

    private final Map<Integer, Boolean> playersById = new ConcurrentHashMap<>();

    public Integer acquireId() {
        for (int id = 1; id <= MAX_PLAYERS; id++) {
            if (playersById.putIfAbsent(id, Boolean.TRUE) == null) {
                return id;
            }
        }
        return null;
    }

    public void release(int id) {
        playersById.remove(id);
    }

    public int getMaxPlayers() {
        return MAX_PLAYERS;
    }

}