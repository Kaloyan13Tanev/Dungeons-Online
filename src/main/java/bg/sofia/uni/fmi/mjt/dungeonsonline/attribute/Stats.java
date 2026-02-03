package bg.sofia.uni.fmi.mjt.dungeonsonline.attribute;

public class Stats {

    public static final int START_HEALTH = 100;
    public static final int START_MANA = 100;
    public static final int START_ATTACK = 50;
    public static final int START_DEFENSE = 50;

    private int health;
    private int mana;
    private int attack;
    private int defense;

    public Stats() {
        this.health = START_HEALTH;
        this.mana = START_MANA;
        this.attack = START_ATTACK;
        this.defense = START_DEFENSE;
    }

    public int getHealth() {
        return health;
    }

    public int getMana() {
        return mana;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }
}
