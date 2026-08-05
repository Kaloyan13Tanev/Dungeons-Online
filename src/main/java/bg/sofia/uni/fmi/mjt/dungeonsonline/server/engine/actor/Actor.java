package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.Position;

public interface Actor {

    int getId();

    Stats getStats();

    Position getPosition();

    boolean isAlive();
}
