package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.Position;

public class Player extends AbstractActor {

    public Player(int id, String name, Position position) {
        super(id, name, new Stats(), position);
    }
}
