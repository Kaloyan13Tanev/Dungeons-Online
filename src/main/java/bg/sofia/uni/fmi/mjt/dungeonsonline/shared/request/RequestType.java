package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request;

public enum RequestType {

    MOVE(MoveRequest.class),
    SELECT(SelectRequest.class),
    USE(UseRequest.class),
    PICK_UP(PickUpRequest.class),
    GIVE(GiveRequest.class),
    DROP(DropRequest.class),
    QUIT(QuitRequest.class);

    private final Class<? extends Request> requestClass;

    RequestType(Class<? extends Request> requestClass) {
        this.requestClass = requestClass;
    }

    public Class<? extends Request> requestClass() {
        return requestClass;
    }

    public static RequestType of(Request request) {
        return switch (request) {
            case MoveRequest ignored -> MOVE;
            case SelectRequest ignored -> SELECT;
            case UseRequest ignored -> USE;
            case PickUpRequest ignored -> PICK_UP;
            case GiveRequest ignored -> GIVE;
            case DropRequest ignored -> DROP;
            case QuitRequest ignored -> QUIT;
        };
    }
}
