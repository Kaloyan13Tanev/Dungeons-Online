package bg.sofia.uni.fmi.mjt.dungeonsonline.server.id;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdPoolTest {

    private static final int PLAYER_COUNT = 9;

    private IdPool pool;

    @BeforeEach
    void setUp() {
        pool = new IdPool(PLAYER_COUNT);
    }

    @Test
    void testConstructorThrowsIfPlayerCountNotPositive() {
        assertThrows(IllegalArgumentException.class, () -> new IdPool(0),
            "Id Pool should throw if playerCount is 0");
        assertThrows(IllegalArgumentException.class, () -> new IdPool(-1),
            "Id Pool should throw if playerCount is negative");
    }

    @Test
    void testAcquireReturnsIdsInRange() {
        Optional<Integer> id1 = pool.acquire();
        Optional<Integer> id2 = pool.acquire();

        assertTrue(id1.isPresent(), "Id Pool should return an id while it has free ones");
        assertTrue(id2.isPresent(), "Id Pool should return an id while it has free ones");
        assertEquals(1, id1.get(), "Id Pool should hand out the lowest free id first");
        assertEquals(2, id2.get(), "Id Pool should hand out the next lowest free id");
    }

    @Test
    void testAcquireReturnsEmptyWhenNoIdsLeft() {
        pool = new IdPool(1);
        Optional<Integer> id1 = pool.acquire();
        Optional<Integer> id2 = pool.acquire();

        assertTrue(id1.isPresent(), "Id Pool should return an id while it has free ones");
        assertEquals(1, id1.get(), "Id Pool should hand out the lowest free id first");
        assertTrue(id2.isEmpty(), "Id Pool should return empty once every id is taken");
    }

    @Test
    void testAcquireHandsOutEveryIdExactlyOnce() {
        Set<Integer> acquired = new HashSet<>();

        for (int i = 0; i < PLAYER_COUNT; i++) {
            Optional<Integer> id = pool.acquire();

            assertTrue(id.isPresent(), "Id Pool should return an id while it has free ones");
            assertTrue(acquired.add(id.get()), "Id Pool should not hand out the same id twice");
        }

        assertEquals(PLAYER_COUNT, acquired.size(), "Id Pool should hand out exactly playerCount ids");
        assertTrue(pool.acquire().isEmpty(), "Id Pool should return empty once every id is taken");
    }

    @Test
    void testAcquireReusesReleasedId() {
        pool = new IdPool(1);
        Optional<Integer> id = pool.acquire();

        assertTrue(id.isPresent(), "Id Pool should return an id while it has free ones");
        pool.release(id.get());

        Optional<Integer> reacquired = pool.acquire();

        assertTrue(reacquired.isPresent(), "Id Pool should hand out an id again once it is released");
        assertEquals(id.get(), reacquired.get(), "Id Pool should hand out the id that was released");
    }

    @Test
    void testReleaseIgnoresAlreadyFreeId() {
        pool = new IdPool(1);
        pool.release(1);
        pool.release(1);

        assertTrue(pool.acquire().isPresent(), "Id Pool should return an id while it has free ones");
        assertTrue(pool.acquire().isEmpty(),
            "Id Pool should not duplicate an id that was released while already free");
    }

    @Test
    void testReleaseIgnoresIdOutOfRange() {
        pool = new IdPool(1);
        pool.release(0);
        pool.release(PLAYER_COUNT + 1);

        assertTrue(pool.acquire().isPresent(), "Id Pool should return an id while it has free ones");
        assertTrue(pool.acquire().isEmpty(), "Id Pool should not accept an id outside its range");
    }

    @Test
    void testCapacityReturnsPlayerCount() {
        assertEquals(PLAYER_COUNT, pool.capacity(),
            "Id Pool should return the playerCount it was built with");
    }

//    @Test
//    void testAcquireNeverHandsOutSameIdToConcurrentCallers() throws InterruptedException {
//        Set<Integer> acquired = ConcurrentHashMap.newKeySet();
//        Set<Integer> duplicates = ConcurrentHashMap.newKeySet();
//        CountDownLatch start = new CountDownLatch(1);
//        CountDownLatch finished = new CountDownLatch(PLAYER_COUNT);
//
//        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
//            for (int i = 0; i < PLAYER_COUNT; i++) {
//                executor.execute(() -> {
//                    try {
//                        start.await();
//                        pool.acquire().ifPresent(id -> {
//                            if (!acquired.add(id)) {
//                                duplicates.add(id);
//                            }
//                        });
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                    } finally {
//                        finished.countDown();
//                    }
//                });
//            }
//
//            start.countDown();
//            assertTrue(finished.await(5, TimeUnit.SECONDS), "Every acquiring thread should finish");
//        }
//
//        assertTrue(duplicates.isEmpty(), "Id Pool should never hand out the same id to two callers");
//        assertEquals(PLAYER_COUNT, acquired.size(),
//            "Id Pool should hand out every id when as many callers as ids compete");
//        assertTrue(pool.acquire().isEmpty(), "Id Pool should return empty once every id is taken");
//    }
}
