package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

public class Stats {

    protected static final int BASE_HEALTH = 100;
    protected static final int BASE_ATTACK = 50;
    protected static final int BASE_DEFENSE = 50;

    protected static final int HEALTH_PER_LEVEL = 10;
    protected static final int ATTACK_PER_LEVEL = 5;
    protected static final int DEFENSE_PER_LEVEL = 5;

    private int health;
    private int maxHealth;
    private int attack;
    private int defense;

    public Stats() {
        this(BASE_HEALTH, BASE_ATTACK, BASE_DEFENSE);
    }

    public Stats(int maxHealth, int attack, int defense) {
        requirePositive(maxHealth, "Max health");
        requireNotNegative(attack, "Attack");
        requireNotNegative(defense, "Defense");

        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.defense = defense;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
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

    public void restore() {
        health = maxHealth;
    }

    protected static void requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive, got " + value);
        }
    }

    protected static void requireNotNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative, got " + value);
        }
    }
}
