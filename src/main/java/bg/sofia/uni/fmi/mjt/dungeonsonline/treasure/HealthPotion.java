package bg.sofia.uni.fmi.mjt.dungeonsonline.treasure;

import java.util.Objects;

public class HealthPotion extends Spell {

    private final int healthCharge;

    public HealthPotion(int manaCost, int healthCharge) {
        super(manaCost);
        this.healthCharge = healthCharge;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HealthPotion that = (HealthPotion) o;
        return healthCharge == that.healthCharge;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), healthCharge);
    }

    public int getHealthCharge() {
        return healthCharge;
    }
}
