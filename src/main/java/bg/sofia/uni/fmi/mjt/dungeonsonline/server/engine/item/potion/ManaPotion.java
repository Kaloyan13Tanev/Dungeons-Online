package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.PlayerStats;

public record ManaPotion(String name, int mana) implements Potion {

    @Override
    public void applyTo(PlayerStats stats) {
        stats.restoreMana(mana);
    }
}
