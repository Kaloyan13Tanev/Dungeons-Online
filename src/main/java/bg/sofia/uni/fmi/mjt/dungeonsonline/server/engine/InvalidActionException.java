package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine;

public abstract class InvalidActionException extends RuntimeException {

    protected InvalidActionException(String message) {
        super(message);
    }

    protected InvalidActionException(String message, Throwable e) {
        super(message, e);
    }

    protected InvalidActionException(Throwable e) {
        super(e);
    }

}
