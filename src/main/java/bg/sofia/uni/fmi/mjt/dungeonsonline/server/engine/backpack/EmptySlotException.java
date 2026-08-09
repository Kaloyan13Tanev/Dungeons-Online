package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.InvalidActionException;

public class EmptySlotException extends InvalidActionException {

    public EmptySlotException(String message) {
        super(message);
    }

    public EmptySlotException(String message, Throwable e) {
        super(message, e);
    }

    public EmptySlotException(Throwable e) {
        super(e);
    }

}
