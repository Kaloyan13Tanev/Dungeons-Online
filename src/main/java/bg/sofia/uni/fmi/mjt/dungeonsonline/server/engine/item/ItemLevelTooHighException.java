package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item;

public class ItemLevelTooHighException extends RuntimeException {

    public ItemLevelTooHighException(String message) {
        super(message);
    }

    public ItemLevelTooHighException(String message, Throwable e) {
        super(message, e);
    }

    public ItemLevelTooHighException(Throwable e) {
        super(e);
    }

}
