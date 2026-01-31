package bg.sofia.uni.fmi.mjt.dungeonsonline.exception;

public class TileNotWalkableException extends RuntimeException {

    public TileNotWalkableException() {
        super();
    }

    public TileNotWalkableException(String message) {
        super(message);
    }

    public TileNotWalkableException(String message, Throwable cause) {
        super(message, cause);
    }

}
