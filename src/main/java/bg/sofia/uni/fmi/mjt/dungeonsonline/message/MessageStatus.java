package bg.sofia.uni.fmi.mjt.dungeonsonline.message;

public enum MessageStatus {

    CLIENT_ACCEPTED(100),
    SUCCESSFUL_REQUEST(200),
    CLIENT_REJECTED(400);

    private final int statusCode;

    MessageStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
