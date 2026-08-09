package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.InvalidActionException;

public class ItemLevelTooHighException extends InvalidActionException {

    public ItemLevelTooHighException(String message) {
        super(message);
    }

    public ItemLevelTooHighException(String message, Throwable e) {
        super(message, e);
    }

    public ItemLevelTooHighException(Throwable e) {
        super(e);
    }

}
