package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.TerrainKind;

import java.util.List;

public record TerrainDTO(List<List<TerrainKind>> tiles) { }
