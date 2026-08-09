package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.InvalidActionException;

public class InvalidMoveException extends InvalidActionException {

    public InvalidMoveException(String message) {
        super(message);
    }

    public InvalidMoveException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidMoveException(Throwable e) {
        super(e);
    }

}
