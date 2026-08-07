package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

public class NotEnoughManaException extends RuntimeException {

    public NotEnoughManaException(String message) {
        super(message);
    }

    public NotEnoughManaException(String message, Throwable cause) {
        super(message, cause);
    }

}
