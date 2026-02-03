package bg.sofia.uni.fmi.mjt.dungeonsonline.entity;

import bg.sofia.uni.fmi.mjt.dungeonsonline.attribute.Level;
import bg.sofia.uni.fmi.mjt.dungeonsonline.attribute.Stats;

public abstract class Entity implements Actor {
    Level level = new Level();
    Stats stats = new Stats();

    public Level getLevel() {
        return level;
    }

    public Stats getStats() {
        return stats;
    }
}
