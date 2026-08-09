package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.InvalidActionException;

public class FullBackpackException extends InvalidActionException {

    public FullBackpackException(String message) {
        super(message);
    }

    public FullBackpackException(String message, Throwable e) {
        super(message, e);
    }

    public FullBackpackException(Throwable e) {
        super(e);
    }

}
