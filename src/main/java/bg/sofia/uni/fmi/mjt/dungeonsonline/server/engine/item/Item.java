package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.Potion;

public sealed interface Item permits Weapon, Spell, Potion {

    String name();
}
