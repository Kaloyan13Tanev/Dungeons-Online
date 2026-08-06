package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatsTest {

    private static final int BASE_HEALTH = 100;
    private static final int BASE_MANA = 100;
    private static final int BASE_ATTACK = 50;
    private static final int BASE_DEFENSE = 50;

    private static final int HEALTH_PER_LEVEL = 10;
    private static final int MANA_PER_LEVEL = 10;
    private static final int ATTACK_PER_LEVEL = 5;
    private static final int DEFENSE_PER_LEVEL = 5;

    private Stats stats;

    @BeforeEach
    void setUp() {
        stats = new Stats();
    }

    @Test
    void testDefaultConstructorStartsAtTheBaseValues() {
        assertEquals(BASE_HEALTH, stats.getMaxHealth(), "Stats should start with the base max health");
        assertEquals(BASE_HEALTH, stats.getHealth(), "Stats should start at full health");
        assertEquals(BASE_MANA, stats.getMaxMana(), "Stats should start with the base max mana");
        assertEquals(BASE_MANA, stats.getMana(), "Stats should start at full mana");
        assertEquals(BASE_ATTACK, stats.getAttack(), "Stats should start with the base attack");
        assertEquals(BASE_DEFENSE, stats.getDefense(), "Stats should start with the base defense");
    }

    @Test
    void testConstructorThrowsWhenMaxHealthNotPositive() {
        assertThrows(IllegalArgumentException.class, () -> new Stats(0, BASE_MANA, BASE_ATTACK, BASE_DEFENSE),
            "Stats should throw when max health is zero");
        assertThrows(IllegalArgumentException.class, () -> new Stats(-1, BASE_MANA, BASE_ATTACK, BASE_DEFENSE),
            "Stats should throw when max health is negative");
    }

    @Test
    void testConstructorThrowsWhenMaxManaNotPositive() {
        assertThrows(IllegalArgumentException.class, () -> new Stats(BASE_HEALTH, 0, BASE_ATTACK, BASE_DEFENSE),
            "Stats should throw when max mana is zero");
        assertThrows(IllegalArgumentException.class, () -> new Stats(BASE_HEALTH, -1, BASE_ATTACK, BASE_DEFENSE),
            "Stats should throw when max mana is negative");
    }

    @Test
    void testConstructorThrowsWhenAttackOrDefenseIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Stats(BASE_HEALTH, BASE_MANA, -1, BASE_DEFENSE),
            "Stats should throw when attack is negative");
        assertThrows(IllegalArgumentException.class, () -> new Stats(BASE_HEALTH, BASE_MANA, BASE_ATTACK, -1),
            "Stats should throw when defense is negative");
    }

    @Test
    void testConstructorAllowsZeroAttackAndDefense() {
        Stats harmless = new Stats(BASE_HEALTH, BASE_MANA, 0, 0);

        assertEquals(0, harmless.getAttack(), "Stats should accept an attack of zero");
        assertEquals(0, harmless.getDefense(), "Stats should accept a defense of zero");
    }

    @Test
    void testIsAliveIsTrueWhileHealthRemains() {
        assertTrue(stats.isAlive(), "Stats should return true while health is above zero");
    }

    @Test
    void testLevelUpRaisesEveryStat() {
        stats.levelUp(1);

        assertEquals(BASE_HEALTH + HEALTH_PER_LEVEL, stats.getMaxHealth(),
            "Stats should raise max health on level up");
        assertEquals(BASE_MANA + MANA_PER_LEVEL, stats.getMaxMana(),
            "Stats should raise max mana on level up");
        assertEquals(BASE_ATTACK + ATTACK_PER_LEVEL, stats.getAttack(),
            "Stats should raise attack on level up");
        assertEquals(BASE_DEFENSE + DEFENSE_PER_LEVEL, stats.getDefense(),
            "Stats should raise defense on level up");
    }

    @Test
    void testLevelUpAppliesEveryLevelItIsGiven() {
        stats.levelUp(3);

        assertEquals(BASE_HEALTH + 3 * HEALTH_PER_LEVEL, stats.getMaxHealth(),
            "Stats should raise max health once per level gained");
        assertEquals(BASE_MANA + 3 * MANA_PER_LEVEL, stats.getMaxMana(),
            "Stats should raise max mana once per level gained");
        assertEquals(BASE_ATTACK + 3 * ATTACK_PER_LEVEL, stats.getAttack(),
            "Stats should raise attack once per level gained");
        assertEquals(BASE_DEFENSE + 3 * DEFENSE_PER_LEVEL, stats.getDefense(),
            "Stats should raise defense once per level gained");
    }

    @Test
    void testLevelUpThrowsOnNegativeLevels() {
        assertThrows(IllegalArgumentException.class, () -> stats.levelUp(-1),
            "Stats should throw when the number of levels is negative");
    }

    @Test
    void testLevelUpLeavesCurrentHealthAndManaUntouched() {
        stats.takeDamage(BASE_HEALTH / 2);
        stats.spendMana(BASE_MANA / 2);

        stats.levelUp(1);

        assertEquals(BASE_HEALTH / 2, stats.getHealth(),
            "Stats should raise only the max health on level up, not the current one");
        assertEquals(BASE_MANA / 2, stats.getMana(),
            "Stats should raise only the max mana on level up, not the current one");
    }

    @Test
    void testLevelUpLeavesRoomToHealUpToTheNewMaximum() {
        stats.levelUp(1);
        stats.heal(HEALTH_PER_LEVEL);
        stats.restoreMana(MANA_PER_LEVEL);

        assertEquals(BASE_HEALTH + HEALTH_PER_LEVEL, stats.getHealth(),
            "Stats should let health be healed up to the raised maximum");
        assertEquals(BASE_MANA + MANA_PER_LEVEL, stats.getMana(),
            "Stats should let mana be restored up to the raised maximum");
    }

    @Test
    void testTakeDamageReducesHealth() {
        stats.takeDamage(30);

        assertEquals(BASE_HEALTH - 30, stats.getHealth(), "Stats should subtract the damage from health");
    }

    @Test
    void testTakeDamageStopsAtZero() {
        stats.takeDamage(BASE_HEALTH + 50);

        assertEquals(0, stats.getHealth(), "Stats should never drop health below zero");
        assertFalse(stats.isAlive(), "Stats should not be alive once health reaches zero");
    }

    @Test
    void testTakeDamageThrowsOnNegativeDamage() {
        assertThrows(IllegalArgumentException.class, () -> stats.takeDamage(-1),
            "Stats should throw when the damage is negative");
    }

    @Test
    void testHealRestoresHealth() {
        stats.takeDamage(40);
        stats.heal(15);

        assertEquals(BASE_HEALTH - 40 + 15, stats.getHealth(), "Stats should add the healing to health");
    }

    @Test
    void testHealStopsAtMaxHealth() {
        stats.takeDamage(10);
        stats.heal(100);

        assertEquals(BASE_HEALTH, stats.getHealth(), "Stats should never heal above max health");
    }

    @Test
    void testHealThrowsOnNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> stats.heal(-1),
            "Stats should throw when the healing amount is negative");
    }

    @Test
    void testRestoreManaStopsAtMaxMana() {
        stats.spendMana(20);
        stats.restoreMana(100);

        assertEquals(BASE_MANA, stats.getMana(), "Stats should never restore mana above max mana");
    }

    @Test
    void testRestoreManaThrowsOnNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> stats.restoreMana(-1),
            "Stats should throw when the mana amount is negative");
    }

    @Test
    void testSpendManaSubtractsTheCost() {
        stats.spendMana(30);

        assertEquals(BASE_MANA - 30, stats.getMana(), "Stats should subtract the spent mana");
    }

    @Test
    void testSpendManaAllowsSpendingEverythingThatIsLeft() {
        stats.spendMana(BASE_MANA);

        assertEquals(0, stats.getMana(), "Stats should allow a cost equal to the mana that is left");
    }

    @Test
    void testSpendManaLeavesManaUntouchedWhenThereIsNotEnough() {
        stats.spendMana(BASE_MANA + 1);

        assertEquals(BASE_MANA, stats.getMana(), "Stats should not spend any mana when the cost is too high");
    }

    @Test
    void testSpendManaNeverDropsManaBelowZero() {
        stats.spendMana(BASE_MANA - 10);
        stats.spendMana(BASE_MANA);

        assertEquals(10, stats.getMana(), "Stats should never let mana go negative");
    }

    @Test
    void testSpendManaThrowsOnNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> stats.spendMana(-1),
            "Stats should throw when the mana cost is negative");
    }

    @Test
    void testRestoreRefillsHealthAndMana() {
        stats.takeDamage(60);
        stats.spendMana(70);

        stats.restore();

        assertEquals(BASE_HEALTH, stats.getHealth(), "Stats should refill health when restored");
        assertEquals(BASE_MANA, stats.getMana(), "Stats should refill mana when restored");
    }

}
