package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.RequestType;

public record RequestDTO(RequestType type, String body) { }
