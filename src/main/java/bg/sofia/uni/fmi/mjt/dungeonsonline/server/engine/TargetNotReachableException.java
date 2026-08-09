package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine;

public class TargetNotReachableException extends InvalidActionException {

    public TargetNotReachableException(String message) {
        super(message);
    }

    public TargetNotReachableException(String message, Throwable e) {
        super(message, e);
    }

    public TargetNotReachableException(Throwable e) {
        super(e);
    }

}
