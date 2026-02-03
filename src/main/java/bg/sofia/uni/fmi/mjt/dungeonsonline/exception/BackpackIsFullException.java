package bg.sofia.uni.fmi.mjt.dungeonsonline.exception;

public class BackpackIsFullException extends RuntimeException {

    public BackpackIsFullException() {
        super();
    }

    public BackpackIsFullException(String message) {
        super(message);
    }

    public BackpackIsFullException(String message, Throwable cause) {
        super(message, cause);
    }
}
