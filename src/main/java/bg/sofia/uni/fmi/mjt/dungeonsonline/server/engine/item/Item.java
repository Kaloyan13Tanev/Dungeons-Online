package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item;

public sealed interface Item permits Weapon, Spell, HealthPotion, ManaPotion {

    String name();
}
