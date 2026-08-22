package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.Mapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.PlayerStats;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.PlayerStateDTO;

import java.util.List;

public class PlayerStateMapper implements Mapper<Player, PlayerStateDTO> {

    private final ItemMapper items;

    public PlayerStateMapper(ItemMapper items) {
        this.items = items;
    }

    @Override
    public PlayerStateDTO toDTO(Player player) {
        PlayerStats stats = player.getStats();
        List<ItemDTO> backpack = items.toDTOs(player.getBackpack().slots());

        return new PlayerStateDTO(
            player.getLevel().getValue(),
            player.getLevel().getXp(),
            player.getLevel().getXpCap(),
            stats.getHealth(),
            stats.getMaxHealth(),
            stats.getMana(),
            stats.getMaxMana(),
            stats.getAttack(),
            stats.getDefense(),
            backpack,
            player.getSelectedSlot()
        );
    }

}
