package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack;

public class EmptySlotException extends RuntimeException {

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
