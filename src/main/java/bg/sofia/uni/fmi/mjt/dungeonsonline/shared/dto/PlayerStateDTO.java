package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import java.util.List;

public record PlayerStateDTO(
    int level,
    int xp,
    int xpCap,
    int health,
    int maxHealth,
    int mana,
    int maxMana,
    int attack,
    int defense,
    List<ItemDTO> backpack,
    int selectedSlot
) {

}
