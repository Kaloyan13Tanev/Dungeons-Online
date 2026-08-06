package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

public class Stats {

    private static final int BASE_HEALTH = 100;
    private static final int BASE_MANA = 100;
    private static final int BASE_ATTACK = 50;
    private static final int BASE_DEFENSE = 50;

    private static final int HEALTH_PER_LEVEL = 10;
    private static final int MANA_PER_LEVEL = 10;
    private static final int ATTACK_PER_LEVEL = 5;
    private static final int DEFENSE_PER_LEVEL = 5;

    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int attack;
    private int defense;

    public Stats() {
        this(BASE_HEALTH, BASE_MANA, BASE_ATTACK, BASE_DEFENSE);
    }

    public Stats(int maxHealth, int maxMana, int attack, int defense) {
        requirePositive(maxHealth, "Max health");
        requirePositive(maxMana, "Max mana");
        requireNotNegative(attack, "Attack");
        requireNotNegative(defense, "Defense");

        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.maxMana = maxMana;
        this.mana = maxMana;
        this.attack = attack;
        this.defense = defense;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void levelUp(int levels) {
        requireNotNegative(levels, "Levels");

        maxHealth += levels * HEALTH_PER_LEVEL;
        maxMana += levels * MANA_PER_LEVEL;
        attack += levels * ATTACK_PER_LEVEL;
        defense += levels * DEFENSE_PER_LEVEL;
    }

    public void takeDamage(int damage) {
        requireNotNegative(damage, "Damage");

        health = Math.max(0, health - damage);
    }

    public void heal(int amount) {
        requireNotNegative(amount, "Amount");

        health = Math.min(maxHealth, health + amount);
    }

    public void restoreMana(int amount) {
        requireNotNegative(amount, "Amount");

        mana = Math.min(maxMana, mana + amount);
    }

    public void spendMana(int amount) {
        requireNotNegative(amount, "Amount");

        if (mana >= amount) {
            mana -= amount;
        }
    }

    public void restore() {
        health = maxHealth;
        mana = maxMana;
    }

    private static void requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive, got " + value);
        }
    }

    private static void requireNotNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative, got " + value);
        }
    }
}
