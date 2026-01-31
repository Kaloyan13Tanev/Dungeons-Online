package bg.sofia.uni.fmi.mjt.dungeonsonline.exception;

public class PlayerLimitReachedException extends RuntimeException {
    public PlayerLimitReachedException() {
        super();
    }

    public PlayerLimitReachedException(String message) {
        super(message);
    }

    public PlayerLimitReachedException(String message, Throwable cause) {
        super(message, cause);
    }
}
