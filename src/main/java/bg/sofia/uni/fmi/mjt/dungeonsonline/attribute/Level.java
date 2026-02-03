package bg.sofia.uni.fmi.mjt.dungeonsonline.attribute;

public class Level {

    public static final int XP_TO_LEVEL_UP = 100;

    private int level;
    private int xpBar;

    public Level() {
        this.level = 0;
        this.xpBar = 0;
    }

    public int getValue() {
        return level;
    }

    public int getXP() {
        return xpBar;
    }

    public boolean isGreaterThan(int value) {
        return level > value;
    }

    public boolean isLessThan(int value) {
        return level < value;
    }

    public boolean isEqualTo(int value) {
        return level == value;
    }

}
