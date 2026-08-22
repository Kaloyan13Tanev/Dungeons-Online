package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LevelTest {

    private static final int STARTING_VALUE = 1;
    private static final int XP_PER_LEVEL = 100;

    private static final int SMALL_CAP = 10;

    private static final int XP_GAIN = 50;
    private static final int LEVELS = 3;
    private static final int REMAINDER = 3;
    private static final int SHORT_OF_THE_CAP = 4;
    private static final int REST_OF_THE_CAP = 6;

    private Level level;

    @BeforeEach
    void setUp() {
        level = new Level();
    }

    @Test
    void testConstructorThrowsWhenValueIsNotPositive() {
        assertThrows(IllegalArgumentException.class, () -> new Level(0),
            "Level should throw when the value is zero");
        assertThrows(IllegalArgumentException.class, () -> new Level(-1),
            "Level should throw when the value is negative");
    }

    @Test
    void testConstructorThrowsWhenXpCapIsNotPositive() {
        assertThrows(IllegalArgumentException.class, () -> new Level(STARTING_VALUE, 0),
            "Level should throw when the xp cap is zero");
        assertThrows(IllegalArgumentException.class, () -> new Level(STARTING_VALUE, -1),
            "Level should throw when the xp cap is negative");
    }

    @Test
    void testAddXpAddsToTheCurrentXp() {
        level.addXp(XP_GAIN);

        assertEquals(XP_GAIN, level.getXp(), "Gained xp should be added to the current xp");
    }

    @Test
    void testAddXpReturnsZeroAndKeepsTheLevelBelowTheCap() {
        int gained = level.addXp(XP_PER_LEVEL - 1);

        assertEquals(0, gained, "Level should report no level gained below the cap");
        assertEquals(STARTING_VALUE, level.getValue(), "Level should not rise when xp is below the cap");
    }

    @Test
    void testAddXpLevelsUpWhenCapIsReached() {
        Level small = new Level(STARTING_VALUE, SMALL_CAP);

        int gained = small.addXp(SMALL_CAP);

        assertEquals(1, gained, "Level should report one level gained at exactly the cap");
        assertEquals(STARTING_VALUE + 1, small.getValue(), "Level should rise at exactly the cap");
        assertEquals(0, small.getXp(), "Level should have no experience left over at exactly the cap");
    }

    @Test
    void testAddXpCarriesTheRemainderIntoTheNewLevel() {
        Level small = new Level(STARTING_VALUE, SMALL_CAP);

        small.addXp(SMALL_CAP + REMAINDER);

        assertEquals(REMAINDER, small.getXp(), "Level should carry the experience past the cap into the new level");
    }

    @Test
    void testAddXpGainsEveryLevelTheAmountCovers() {
        Level small = new Level(STARTING_VALUE, SMALL_CAP);

        int gained = small.addXp(SMALL_CAP * LEVELS + REMAINDER);

        assertEquals(LEVELS, gained, "Level should report every level the gain covers");
        assertEquals(STARTING_VALUE + LEVELS, small.getValue(),
            "Level should rise once per cap the gain covers");
    }

    @Test
    void testAddXpLevelsUpFromRepeatedGainsBelowTheCap() {
        Level small = new Level(STARTING_VALUE, SMALL_CAP);

        assertEquals(0, small.addXp(SMALL_CAP - SHORT_OF_THE_CAP), "Level should not rise on a gain below the cap");
        assertEquals(1, small.addXp(REST_OF_THE_CAP), "Level should rise once the gains together reach the cap");
        assertEquals(2, small.getXp(), "Level should carry what the gains left over the cap");
    }

    @Test
    void testAddXpThrowsOnNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> level.addXp(-1),
            "Level should throw when the experience gained is negative");
    }

}
