package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MinionTest {

    private static final int MINION_ID = 10;

    private static final Position POSITION = new Position(0, 0);

    private static final int FIRST_LEVEL = 1;
    private static final int THIRD_LEVEL = 3;

    private static final int BASE_XP_REWARD = 50;
    private static final int XP_REWARD_PER_LEVEL = 5;

    @Mock
    private Stats stats;
    @Mock
    private Level level;

    private Minion minion;

    @BeforeEach
    void setUp() {
        minion = new Minion(MINION_ID, POSITION, stats, level);
    }

    @Test
    void testGetXpRewardIsTheBaseAtTheFirstLevel() {
        when(level.getValue()).thenReturn(FIRST_LEVEL);

        assertEquals(BASE_XP_REWARD, minion.getXpReward(),
            "Minion should reward the base experience at the first level");
    }

    @Test
    void testGetXpRewardGrowsWithEveryLevelAboveTheFirst() {
        when(level.getValue()).thenReturn(THIRD_LEVEL);

        assertEquals(BASE_XP_REWARD + 2 * XP_REWARD_PER_LEVEL, minion.getXpReward(),
            "Minion should reward more experience for each level above the first");
    }

}
