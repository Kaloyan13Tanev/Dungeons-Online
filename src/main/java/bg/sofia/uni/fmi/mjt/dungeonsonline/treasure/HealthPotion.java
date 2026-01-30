package bg.sofia.uni.fmi.mjt.dungeonsonline.treasure;

public class HealthPotion extends Spell {

    private final int healthCharge;

    public HealthPotion(int manaCost, int healthCharge) {
        super(manaCost);
        this.healthCharge = healthCharge;
    }

}
