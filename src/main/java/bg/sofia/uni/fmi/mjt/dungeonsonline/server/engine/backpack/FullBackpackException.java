package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack;

public class FullBackpackException extends RuntimeException {

    public FullBackpackException(String message) {
        super(message);
    }

    public FullBackpackException(String message, Throwable cause) {
        super(message, cause);
    }

    public FullBackpackException(Throwable cause) {
        super(cause);
    }

}
