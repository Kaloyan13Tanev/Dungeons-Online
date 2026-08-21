package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ManaPotionTest {

    private static final String NAME = "Elixir";
    private static final int MANA = 30;

    @Mock
    private PlayerStats stats;

    private ManaPotion potion;

    @BeforeEach
    void setUp() {
        potion = new ManaPotion(NAME, MANA);
    }

    @Test
    void testApplyToRestoresTheManaItHolds() {
        potion.applyTo(stats);

        verify(stats).restoreMana(MANA);
    }

}
