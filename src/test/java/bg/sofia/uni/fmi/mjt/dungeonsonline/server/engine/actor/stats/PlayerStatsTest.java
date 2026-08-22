package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerStatsTest {

    private static final int BASE_HEALTH = 100;
    private static final int BASE_MANA = 100;
    private static final int BASE_ATTACK = 50;
    private static final int BASE_DEFENSE = 50;

    private static final int MANA_PER_LEVEL = 10;

    private static final int LEVELS = 3;

    private static final int MANA_COST = 30;
    private static final int SMALL_MANA_COST = 20;
    private static final int LARGE_MANA_COST = 70;
    private static final int MANA_ABOVE_THE_CAP = 100;
    private static final int DAMAGE = 60;

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
        stats.levelUp(LEVELS);

        assertEquals(BASE_MANA + LEVELS * MANA_PER_LEVEL, stats.getMaxMana(),
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
        stats.spendMana(MANA_COST);

        assertEquals(BASE_MANA - MANA_COST, stats.getMana(), "Player stats should subtract the spent mana");
    }

    @Test
    void testSpendManaAllowsSpendingEverythingThatIsLeft() {
        stats.spendMana(BASE_MANA);

        assertEquals(0, stats.getMana(), "Player stats should allow a cost equal to the mana that is left");
    }

    @Test
    void testSpendManaThrowsAndLeavesManaUntouchedWhenThereIsNotEnough() {
        assertThrows(NotEnoughManaException.class, () -> stats.spendMana(BASE_MANA + 1),
            "Player stats should throw when the cost is higher than the mana that is left");
        assertEquals(BASE_MANA, stats.getMana(),
            "Player stats should not spend any mana when the cost is too high");
    }

    @Test
    void testHasManaCoversCostsUpToTheManaThatIsLeft() {
        assertTrue(stats.hasMana(BASE_MANA),
            "Player stats should report a cost equal to the mana that is left as affordable");
        assertFalse(stats.hasMana(BASE_MANA + 1),
            "Player stats should report a cost above the mana that is left as unaffordable");
    }

    @Test
    void testSpendManaThrowsOnNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> stats.spendMana(-1),
            "Player stats should throw when the mana cost is negative");
    }

    @Test
    void testRestoreManaCaps() {
        stats.spendMana(SMALL_MANA_COST);
        stats.restoreMana(MANA_ABOVE_THE_CAP);

        assertEquals(BASE_MANA, stats.getMana(), "Player stats should never restore mana above the cap");
    }

    @Test
    void testRestoreManaThrowsOnNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> stats.restoreMana(-1),
            "Player stats should throw when the mana amount is negative");
    }

    @Test
    void testRestoreRefillsHealthAndMana() {
        stats.takeDamage(DAMAGE);
        stats.spendMana(LARGE_MANA_COST);

        stats.restore();

        assertEquals(BASE_HEALTH, stats.getHealth(), "Player stats should refill health when restored");
        assertEquals(BASE_MANA, stats.getMana(), "Player stats should refill mana when restored");
    }

}
