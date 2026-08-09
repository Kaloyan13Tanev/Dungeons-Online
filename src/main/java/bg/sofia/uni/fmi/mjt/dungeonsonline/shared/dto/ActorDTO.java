package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;

public record ActorDTO(int id, ActorKind kind, int row, int col) { }
