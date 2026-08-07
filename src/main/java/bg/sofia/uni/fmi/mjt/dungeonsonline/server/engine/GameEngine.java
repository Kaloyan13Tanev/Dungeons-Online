package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;

public interface GameEngine {

    void join(int playerId);

    void leave(int playerId);

    void move(int playerId, Direction direction);

    void select(int playerId, int slot);

    void use(int playerId, Integer targetId);

    void pickUp(int playerId, int treasureId);

    void give(int playerId, int targetPlayerId);

    void drop(int playerId);
}
