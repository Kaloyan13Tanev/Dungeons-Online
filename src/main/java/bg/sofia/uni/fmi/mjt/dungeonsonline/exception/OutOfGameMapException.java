package bg.sofia.uni.fmi.mjt.dungeonsonline.exception;

public class OutOfGameMapException extends RuntimeException {

    public OutOfGameMapException() {
        super();
    }

    public OutOfGameMapException(String message) {
        super(message);
    }

    public OutOfGameMapException(String message, Throwable cause) {
        super(message, cause);
    }

}
