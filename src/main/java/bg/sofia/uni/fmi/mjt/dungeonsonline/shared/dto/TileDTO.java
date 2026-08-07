package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import java.util.List;

public record TileDTO(boolean walkable, List<Integer> playerIds, boolean hasMinion, List<String> itemKinds) { }
