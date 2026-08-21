package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;

public class Minion extends AbstractActor {

    private static final int BASE_XP_REWARD = 50;
    private static final int XP_REWARD_PER_LEVEL = 5;

    private final Level level;

    public Minion(int id, int level, Position position) {
        Level levelInst = new Level(level);

        this(id, position, new Stats(levelInst), levelInst);
    }

    public Minion(int id, Position position, Stats stats, Level level) {
        super(id, stats, position);

        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public int getXpReward() {
        return BASE_XP_REWARD + (level.getValue() - 1) * XP_REWARD_PER_LEVEL;
    }

}
