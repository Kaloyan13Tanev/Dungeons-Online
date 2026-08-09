package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.PlayerStats;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;

public sealed interface Potion extends Item permits HealthPotion, ManaPotion {

    void applyTo(PlayerStats stats);

}
