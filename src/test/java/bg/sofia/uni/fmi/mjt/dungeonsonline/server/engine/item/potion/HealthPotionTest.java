package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HealthPotionTest {

    private static final String NAME = "Bandage";
    private static final int HEALING = 30;

    @Mock
    private PlayerStats stats;

    private HealthPotion potion;

    @BeforeEach
    void setUp() {
        potion = new HealthPotion(NAME, HEALING);
    }

    @Test
    void testApplyToHealsForTheHealingItHolds() {
        potion.applyTo(stats);

        verify(stats).heal(HEALING);
    }

}
