package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.InvalidActionException;

public class NotEnoughManaException extends InvalidActionException {

    public NotEnoughManaException(String message) {
        super(message);
    }

    public NotEnoughManaException(String message, Throwable e) {
        super(message, e);
    }

    public NotEnoughManaException(Throwable e) {
        super(e);
    }

}
