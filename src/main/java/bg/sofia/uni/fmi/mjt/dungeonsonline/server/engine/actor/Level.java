package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

public class Level {

    private static final int STARTING_LEVEL = 1;
    private static final int EXPERIENCE_PER_LEVEL = 100;

    private int value;
    private int xp;
    private int xpCap;

    public Level() {
        this.value = STARTING_LEVEL;
        this.xp = 0;
        this.xpCap = EXPERIENCE_PER_LEVEL;
    }

    public Level(int value) {
        this(value, EXPERIENCE_PER_LEVEL);
    }

    public Level(int value, int xpCap) {
        if (value < STARTING_LEVEL) {
            throw new IllegalArgumentException("Value must be at least " + STARTING_LEVEL + ", got " + value);
        }

        if (xpCap < 1) {
            throw new IllegalArgumentException("Xp cap must be positive, got " + xpCap);
        }

        this.value = value;
        this.xp = 0;
        this.xpCap = xpCap;
    }

    public int getValue() {
        return value;
    }

    public int getXp() {
        return xp;
    }

    public int getXpCap() {
        return xpCap;
    }

    public int addXp(int xpToAdd) {
        if (xpToAdd < 0) {
            throw new IllegalArgumentException("XP amount must not be negative, got " + xpToAdd);
        }

        xp += xpToAdd;

        int gained = xp / xpCap;
        xp %= xpCap;
        value += gained;

        return gained;
    }

}
