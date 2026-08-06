package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerStatsTest {

    private static final int BASE_HEALTH = 100;
    private static final int BASE_MANA = 100;
    private static final int BASE_ATTACK = 50;
    private static final int BASE_DEFENSE = 50;

    private static final int MANA_PER_LEVEL = 10;

    private PlayerStats stats;

    @BeforeEach
    void setUp() {
        stats = new PlayerStats();
    }

    @Test
    void testConstructorThrowsWhenMaxManaNotPositive() {
        assertThrows(IllegalArgumentException.class,
            () -> new PlayerStats(BASE_HEALTH, 0, BASE_ATTACK, BASE_DEFENSE),
            "Player stats should throw when max mana is zero");
        assertThrows(IllegalArgumentException.class,
            () -> new PlayerStats(BASE_HEALTH, -1, BASE_ATTACK, BASE_DEFENSE),
            "Player stats should throw when max mana is negative");
    }

    @Test
    void testLevelUpRaisesMaxMana() {
        stats.levelUp(3);

        assertEquals(BASE_MANA + 3 * MANA_PER_LEVEL, stats.getMaxMana(),
            "Player stats should raise max mana once per level gained");
    }

    @Test
    void testLevelUpLeavesCurrentManaUntouched() {
        stats.spendMana(BASE_MANA / 2);

        stats.levelUp(1);

        assertEquals(BASE_MANA / 2, stats.getMana(),
            "Player stats should raise only the max mana on level up, not the current one");
    }

    @Test
    void testSpendManaSubtractsTheCost() {
        stats.spendMana(30);

        assertEquals(BASE_MANA - 30, stats.getMana(), "Player stats should subtract the spent mana");
    }

    @Test
    void testSpendManaAllowsSpendingEverythingThatIsLeft() {
        stats.spendMana(BASE_MANA);

        assertEquals(0, stats.getMana(), "Player stats should allow a cost equal to the mana that is left");
    }

    @Test
    void testSpendManaLeavesManaUntouchedWhenThereIsNotEnough() {
        stats.spendMana(BASE_MANA + 1);

        assertEquals(BASE_MANA, stats.getMana(),
            "Player stats should not spend any mana when the cost is too high");
    }

    @Test
    void testSpendManaThrowsOnNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> stats.spendMana(-1),
            "Player stats should throw when the mana cost is negative");
    }

    @Test
    void testRestoreManaCaps() {
        stats.spendMana(20);
        stats.restoreMana(100);

        assertEquals(BASE_MANA, stats.getMana(), "Player stats should never restore mana above the cap");
    }

    @Test
    void testRestoreManaThrowsOnNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> stats.restoreMana(-1),
            "Player stats should throw when the mana amount is negative");
    }

    @Test
    void testRestoreRefillsHealthAndMana() {
        stats.takeDamage(60);
        stats.spendMana(70);

        stats.restore();

        assertEquals(BASE_HEALTH, stats.getHealth(), "Player stats should refill health when restored");
        assertEquals(BASE_MANA, stats.getMana(), "Player stats should refill mana when restored");
    }

}
