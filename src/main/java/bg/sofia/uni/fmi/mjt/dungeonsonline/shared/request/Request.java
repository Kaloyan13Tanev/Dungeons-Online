package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request;

public sealed interface Request permits MoveRequest, SelectRequest, UseRequest, PickUpRequest, GiveRequest,
    DropRequest, QuitRequest {

}
