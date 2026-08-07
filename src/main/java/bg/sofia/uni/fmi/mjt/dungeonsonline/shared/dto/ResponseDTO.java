package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.ResponseType;

public record ResponseDTO(ResponseType type, String body) { }
