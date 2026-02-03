package bg.sofia.uni.fmi.mjt.dungeonsonline.treasure;

import java.util.Objects;

public class ManaPotion extends Spell {

    private final int manaCharge;

    public ManaPotion(int manaCharge) {
        super(0);
        this.manaCharge = manaCharge;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ManaPotion that = (ManaPotion) o;
        return manaCharge == that.manaCharge;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), manaCharge);
    }

    public int getManaCharge() {
        return manaCharge;
    }
}
