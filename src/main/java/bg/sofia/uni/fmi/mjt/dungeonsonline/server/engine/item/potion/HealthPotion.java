package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.PlayerStats;

public record HealthPotion(String name, int healing) implements Potion {

    @Override
    public void applyTo(PlayerStats stats) {
        stats.heal(healing);
    }

}
