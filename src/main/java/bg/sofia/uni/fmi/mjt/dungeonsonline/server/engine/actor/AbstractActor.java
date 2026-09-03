package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.stats.Stats;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;

import java.util.Objects;

public abstract class AbstractActor implements Actor {

    private final int id;
    private final Stats stats;

    private Position position;

    protected AbstractActor(int id, Stats stats, Position position) {
        this.id = id;
        this.stats = stats;
        this.position = position;
    }

    @Override
    public int getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractActor that = (AbstractActor) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
