package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.Position;

public class Player extends AbstractActor {

    public Player(int id, Position position) {
        super(id, new Stats(), position);
    }
}
