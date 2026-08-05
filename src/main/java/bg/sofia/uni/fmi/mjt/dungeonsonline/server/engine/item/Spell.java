package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item;

public record Spell(String name, int level, int damage, int manaCost) implements Item {
}
