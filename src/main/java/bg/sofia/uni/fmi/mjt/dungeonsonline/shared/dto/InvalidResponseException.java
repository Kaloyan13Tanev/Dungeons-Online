package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

public class InvalidResponseException extends RuntimeException {

    public InvalidResponseException(String message) {
        super(message);
    }

    public InvalidResponseException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidResponseException(Throwable e) {
        super(e);
    }

}
