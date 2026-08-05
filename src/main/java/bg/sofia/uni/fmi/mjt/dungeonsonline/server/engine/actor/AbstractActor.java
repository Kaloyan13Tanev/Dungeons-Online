package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.Position;

public abstract class AbstractActor implements Actor {

    private final int id;
    private final String name;
    private final Stats stats;

    private Position position;

    protected AbstractActor(int id, String name, Stats stats, Position position) {
        this.id = id;
        this.name = name;
        this.stats = stats;
        this.position = position;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Stats getStats() {
        return stats;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public boolean isAlive() {
        return stats.isAlive();
    }

    public void moveTo(Position position) {
        this.position = position;
    }
}
