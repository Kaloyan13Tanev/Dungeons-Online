package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack;

public class FullBackpackException extends RuntimeException {

    public FullBackpackException(String message) {
        super(message);
    }

    public FullBackpackException(String message, Throwable e) {
        super(message, e);
    }

    public FullBackpackException(Throwable e) {
        super(e);
    }

}
