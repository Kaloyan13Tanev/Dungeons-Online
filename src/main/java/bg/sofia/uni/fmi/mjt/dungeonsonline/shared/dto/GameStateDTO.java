package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import java.util.List;

public record GameStateDTO(
    List<ActorDTO> actors,
    List<TreasureDTO> treasures,
    PlayerStateDTO player
) {

}
