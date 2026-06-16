package bg.sofia.uni.fmi.mjt.dungeonsonline.common.request;

public sealed interface Request
        permits MoveRequest, QuitRequest { }
