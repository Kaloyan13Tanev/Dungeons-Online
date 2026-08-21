package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.Backpack;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerTest {

    private static final int PLAYER_ID = 1;

    private static final Position START = new Position(0, 0);

    private static final int BACKPACK_CAPACITY = 10;
    private static final int SECOND_SLOT = 1;

    private static final int XP_GAIN = 50;
    private static final int NO_LEVELS = 0;
    private static final int LEVELS_GAINED = 2;

    @Mock
    private PlayerStats stats;
    @Mock
    private Level level;
    @Mock
    private Backpack backpack;

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(PLAYER_ID, START, stats, level, backpack);
    }

    @Test
    void testSelectChangesTheSelectedSlot() {
        when(backpack.capacity()).thenReturn(BACKPACK_CAPACITY);

        player.select(SECOND_SLOT);

        assertEquals(SECOND_SLOT, player.getSelectedSlot(), "Player should select the slot they asked for");
    }

    @Test
    void testSelectThrowsForASlotOutOfBounds() {
        when(backpack.capacity()).thenReturn(BACKPACK_CAPACITY);

        assertThrows(IllegalArgumentException.class, () -> player.select(-1),
            "Player should throw when the slot is negative");
        assertThrows(IllegalArgumentException.class, () -> player.select(BACKPACK_CAPACITY),
            "Player should throw when the slot is past the last one");
    }

    @Test
    void testGainExperienceHandsTheAmountToTheLevel() {
        player.gainExperience(XP_GAIN);

        verify(level).addXp(XP_GAIN);
    }

    @Test
    void testGainExperienceRaisesTheStatsByTheLevelsGained() {
        when(level.addXp(XP_GAIN)).thenReturn(LEVELS_GAINED);

        player.gainExperience(XP_GAIN);

        verify(stats).levelUp(LEVELS_GAINED);
    }

    @Test
    void testGainExperienceReturnsTheLevelsGained() {
        when(level.addXp(XP_GAIN)).thenReturn(LEVELS_GAINED);

        assertEquals(LEVELS_GAINED, player.gainExperience(XP_GAIN),
            "Player should report the levels the gain covered");
    }

}
