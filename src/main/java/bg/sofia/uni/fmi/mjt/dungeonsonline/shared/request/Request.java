package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request;

public sealed interface Request permits MoveRequest, PickUpRequest, GiveRequest, AttackRequest, CastRequest,
    DropRequest, QuitRequest {
}
