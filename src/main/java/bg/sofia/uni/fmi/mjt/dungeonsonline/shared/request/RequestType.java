package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request;

public enum RequestType {

    MOVE(MoveRequest.class),
    PICK_UP(PickUpRequest.class),
    GIVE(GiveRequest.class),
    ATTACK(AttackRequest.class),
    CAST(CastRequest.class),
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
            case PickUpRequest ignored -> PICK_UP;
            case GiveRequest ignored -> GIVE;
            case AttackRequest ignored -> ATTACK;
            case CastRequest ignored -> CAST;
            case DropRequest ignored -> DROP;
            case QuitRequest ignored -> QUIT;
        };
    }
}
