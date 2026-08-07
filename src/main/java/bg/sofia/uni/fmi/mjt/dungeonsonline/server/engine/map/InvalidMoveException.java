package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

public class InvalidMoveException extends RuntimeException {

    public InvalidMoveException(String message) {
        super(message);
    }

    public InvalidMoveException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidMoveException(Throwable e) {
        super(e);
    }

}
