package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response;

public record HandshakeResponse(boolean accepted, int playerId, String reason) implements Response { }
