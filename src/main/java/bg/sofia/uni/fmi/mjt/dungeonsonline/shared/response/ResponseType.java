package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response;

public enum ResponseType {

    HANDSHAKE(HandshakeResponse.class),
    STATE(StateResponse.class),
    EVENT(EventResponse.class),
    ERROR(ErrorResponse.class);

    private final Class<? extends Response> responseClass;

    ResponseType(Class<? extends Response> responseClass) {
        this.responseClass = responseClass;
    }

    public Class<? extends Response> responseClass() {
        return responseClass;
    }

    public static ResponseType of(Response response) {
        return switch (response) {
            case HandshakeResponse ignored -> HANDSHAKE;
            case StateResponse ignored -> STATE;
            case EventResponse ignored -> EVENT;
            case ErrorResponse ignored -> ERROR;
        };
    }
}
