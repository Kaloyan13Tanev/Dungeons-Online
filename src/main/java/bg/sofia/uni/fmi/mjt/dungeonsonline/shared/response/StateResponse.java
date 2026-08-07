package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;

public record StateResponse(GameStateDTO state) implements Response { }
