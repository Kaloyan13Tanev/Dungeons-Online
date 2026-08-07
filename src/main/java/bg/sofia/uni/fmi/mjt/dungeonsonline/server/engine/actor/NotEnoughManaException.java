package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

public class NotEnoughManaException extends RuntimeException {

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
