package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidRequestException(Throwable e) {
        super(e);
    }

}
