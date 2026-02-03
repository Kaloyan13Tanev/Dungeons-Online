package bg.sofia.uni.fmi.mjt.dungeonsonline.exception;

public class ItemLevelTooHighException extends RuntimeException {

    public ItemLevelTooHighException() {
        super();
    }

    public ItemLevelTooHighException(String message) {
        super(message);
    }

    public ItemLevelTooHighException(String message, Throwable cause) {
        super(message, cause);
    }

}
