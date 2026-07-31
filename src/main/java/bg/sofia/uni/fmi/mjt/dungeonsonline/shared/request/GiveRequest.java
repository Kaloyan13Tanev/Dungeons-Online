package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request;

public record GiveRequest(int targetPlayerId, int itemId) implements Request { }
