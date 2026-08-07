package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import java.util.List;

public record GameStateDTO(
    List<List<TileDTO>> grid,
    PlayerStateDTO player,
    List<ActorDTO> actorsOnTile,
    List<TreasureDTO> treasuresOnTile) { }
