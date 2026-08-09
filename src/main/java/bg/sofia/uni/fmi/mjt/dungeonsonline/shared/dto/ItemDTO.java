package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;

public record ItemDTO(ItemKind kind, String name, int level, int power, int manaCost) { }
