package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;

public class Minion extends AbstractActor {

    private static final int MIN_LEVEL = 1;

    private static final int BASE_XP_REWARD = 50;
    private static final int XP_REWARD_PER_LEVEL = 5;

    private final Level level;

    public Minion(int id, int level, Position position) {
        super(id, statsFor(level), position);

        this.level = new Level(level);
    }

    public Level getLevel() {
        return level;
    }

    public int getXpReward() {
        return BASE_XP_REWARD + (level.getValue() - MIN_LEVEL) * XP_REWARD_PER_LEVEL;
    }

    private static Stats statsFor(int level) {
        if (level < MIN_LEVEL) {
            throw new IllegalArgumentException("Level must be at least " + MIN_LEVEL + ", got " + level);
        }

        Stats stats = new Stats();
        stats.levelUp(level - MIN_LEVEL);
        stats.restore();

        return stats;
    }
}
