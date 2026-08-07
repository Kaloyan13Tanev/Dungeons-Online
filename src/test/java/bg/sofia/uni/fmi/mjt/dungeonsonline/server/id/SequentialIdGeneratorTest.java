package bg.sofia.uni.fmi.mjt.dungeonsonline.server.id;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SequentialIdGeneratorTest {

    private static final int FIRST_ID = 0;
    private static final int ID_COUNT = 100;

    private SequentialIdGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new SequentialIdGenerator();
    }

    @Test
    void testAcquireStartsFromTheFirstId() {
        assertEquals(FIRST_ID, generator.acquire(),
            "Sequential id generator should hand out the first id to the first caller");
    }

    @Test
    void testAcquireHandsOutConsecutiveIds() {
        assertEquals(FIRST_ID, generator.acquire(),
            "Sequential id generator should hand out the first id to the first caller");
        assertEquals(FIRST_ID + 1, generator.acquire(),
            "Sequential id generator should hand out the next id to the second caller");
        assertEquals(FIRST_ID + 2, generator.acquire(),
            "Sequential id generator should hand out the next id to the third caller");
    }

    @Test
    void testAcquireNeverHandsOutTheSameIdTwice() {
        Set<Integer> acquired = new HashSet<>();

        for (int i = 0; i < ID_COUNT; i++) {
            assertTrue(acquired.add(generator.acquire()),
                "Sequential id generator should not hand out the same id twice");
        }

        assertEquals(ID_COUNT, acquired.size(),
            "Sequential id generator should hand out one id per call");
    }

    @Test
    void testAcquireKeepsCountingAfterManyIds() {
        for (int i = 0; i < ID_COUNT; i++) {
            generator.acquire();
        }

        assertEquals(FIRST_ID + ID_COUNT, generator.acquire(),
            "Sequential id generator should continue from the last id it handed out");
    }

    @Test
    void testGeneratorsCountIndependently() {
        SequentialIdGenerator other = new SequentialIdGenerator();

        generator.acquire();
        generator.acquire();

        assertEquals(FIRST_ID, other.acquire(),
            "Sequential id generator should not share its count with another generator");
    }

}
