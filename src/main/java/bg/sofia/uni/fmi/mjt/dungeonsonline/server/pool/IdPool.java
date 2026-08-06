package bg.sofia.uni.fmi.mjt.dungeonsonline.server.pool;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

public class IdPool {

    public static final int MIN_ID = 1;

    private final int playerCount;
    private final Queue<Integer> pool;

    public IdPool(int playerCount) {
        if (playerCount < 1) {
            throw new IllegalArgumentException("Player count must be positive, got " + playerCount);
        }

        this.playerCount = playerCount;
        this.pool = new PriorityQueue<>(playerCount);
        for (int id = MIN_ID; id <= maxId(); id++) {
            pool.add(id);
        }
    }

    public synchronized Optional<Integer> acquire() {
        return Optional.ofNullable(pool.poll());
    }

    public synchronized void release(int id) {
        if (id < MIN_ID || id > maxId()) {
            return;
        }

        if (pool.contains(id)) {
            return;
        }

        pool.add(id);
    }

    public int capacity() {
        return playerCount;
    }

    private int maxId() {
        return MIN_ID + playerCount - 1;
    }
}
