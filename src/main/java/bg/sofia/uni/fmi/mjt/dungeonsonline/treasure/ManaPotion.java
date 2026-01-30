package bg.sofia.uni.fmi.mjt.dungeonsonline.treasure;

public class ManaPotion extends Spell {

    private final int manaCharge;

    public ManaPotion(int manaCost, int manaCharge) {
        super(manaCost);
        this.manaCharge = manaCharge;
    }
}
