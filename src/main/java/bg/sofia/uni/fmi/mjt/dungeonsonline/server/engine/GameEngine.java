package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;

import java.util.List;

public interface GameEngine {

    List<GameEvent> join(int playerId);

    List<GameEvent> leave(int playerId);

    List<GameEvent> move(int playerId, Direction direction);

    List<GameEvent> select(int playerId, int slot);

    List<GameEvent> use(int playerId, Integer targetId);

    List<GameEvent> pickUp(int playerId, int treasureId);

    List<GameEvent> give(int playerId, int targetPlayerId);

    List<GameEvent> drop(int playerId);
}
