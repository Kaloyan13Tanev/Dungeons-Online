package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

public class Level {

    private static final int STARTING_VALUE = 1;
    private static final int EXPERIENCE_PER_LEVEL = 100;

    private int value;
    private int xp;

    public Level() {
        this(STARTING_VALUE);
    }

    public Level(int value) {
        if (value < STARTING_VALUE) {
            throw new IllegalArgumentException("value must be at least " + STARTING_VALUE + ", got " + value);
        }

        this.value = value;
        this.xp = 0;
    }

    public int getValue() {
        return value;
    }

    public int getXp() {
        return xp;
    }

    public int getXpCap() {
        return EXPERIENCE_PER_LEVEL;
    }

    public int gain(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }

        xp += amount;

        int gained = xp / EXPERIENCE_PER_LEVEL;
        xp %= EXPERIENCE_PER_LEVEL;
        value += gained;

        return gained;
    }
}
