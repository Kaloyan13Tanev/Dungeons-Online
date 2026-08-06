package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

public class PlayerStats extends Stats {

    private static final int BASE_MANA = 100;
    private static final int MANA_PER_LEVEL = 10;

    private int mana;
    private int maxMana;

    public PlayerStats() {
        this(BASE_HEALTH, BASE_MANA, BASE_ATTACK, BASE_DEFENSE);
    }

    public PlayerStats(int maxHealth, int maxMana, int attack, int defense) {
        super(maxHealth, attack, defense);

        requirePositive(maxMana, "Max mana");

        this.maxMana = maxMana;
        this.mana = maxMana;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    @Override
    public void levelUp(int levels) {
        super.levelUp(levels);

        maxMana += levels * MANA_PER_LEVEL;
    }

    @Override
    public void restore() {
        super.restore();

        mana = maxMana;
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
}
