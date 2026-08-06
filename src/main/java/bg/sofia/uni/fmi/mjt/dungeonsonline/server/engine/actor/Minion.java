package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.Position;

public class Minion extends AbstractActor {

    private static final int MIN_LEVEL = 1;

    private static final int BASE_HEALTH = 50;
    private static final int HEALTH_PER_LEVEL = 20;
    private static final int BASE_ATTACK = 20;
    private static final int ATTACK_PER_LEVEL = 10;
    private static final int BASE_DEFENSE = 10;
    private static final int DEFENSE_PER_LEVEL = 5;

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

        return new Stats(
            BASE_HEALTH + level * HEALTH_PER_LEVEL,
            BASE_ATTACK + level * ATTACK_PER_LEVEL,
            BASE_DEFENSE + level * DEFENSE_PER_LEVEL);
    }
}
