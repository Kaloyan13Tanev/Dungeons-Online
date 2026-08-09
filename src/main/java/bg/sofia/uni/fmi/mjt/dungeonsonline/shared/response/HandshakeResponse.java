package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;

public record HandshakeResponse(boolean accepted, int playerId, String reason, TerrainDTO terrain)
    implements Response { }
