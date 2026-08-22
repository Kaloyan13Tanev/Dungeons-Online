package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Level;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.PlayerStats;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.Backpack;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.PlayerStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerStateMapperTest {

    private static final int LEVEL = 2;
    private static final int XP = 40;
    private static final int XP_CAP = 100;

    private static final int HEALTH = 70;
    private static final int MAX_HEALTH = 110;
    private static final int MANA = 60;
    private static final int MAX_MANA = 120;
    private static final int ATTACK = 55;
    private static final int DEFENSE = 45;

    private static final int SELECTED_SLOT = 3;

    private static final ItemDTO ITEM_DTO = new ItemDTO(ItemKind.WEAPON, "Sword", 1, 20, 0);
    private static final List<ItemDTO> BACKPACK_DTO = List.of(ITEM_DTO);

    @Mock
    private ItemMapper items;

    @Mock
    private Player player;
    @Mock
    private PlayerStats stats;
    @Mock
    private Level level;
    @Mock
    private Backpack backpack;
    @Mock
    private Item item;

    private PlayerStateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PlayerStateMapper(items);
    }

    @Test
    void testToDTOMapsEveryStatOfThePlayer() {
        mockPlayer();

        assertEquals(new PlayerStateDTO(LEVEL, XP, XP_CAP, HEALTH, MAX_HEALTH, MANA, MAX_MANA,
                ATTACK, DEFENSE, BACKPACK_DTO, SELECTED_SLOT),
            mapper.toDTO(player),
            "PlayerStateMapper should map the level, the stats, the backpack and the selected slot");
    }

    private void mockPlayer() {
        when(player.getLevel()).thenReturn(level);
        when(level.getValue()).thenReturn(LEVEL);
        when(level.getXp()).thenReturn(XP);
        when(level.getXpCap()).thenReturn(XP_CAP);

        when(player.getStats()).thenReturn(stats);
        when(stats.getHealth()).thenReturn(HEALTH);
        when(stats.getMaxHealth()).thenReturn(MAX_HEALTH);
        when(stats.getMana()).thenReturn(MANA);
        when(stats.getMaxMana()).thenReturn(MAX_MANA);
        when(stats.getAttack()).thenReturn(ATTACK);
        when(stats.getDefense()).thenReturn(DEFENSE);

        when(player.getBackpack()).thenReturn(backpack);
        when(backpack.slots()).thenReturn(List.of(item));
        when(items.toDTOs(List.of(item))).thenReturn(BACKPACK_DTO);

        when(player.getSelectedSlot()).thenReturn(SELECTED_SLOT);
    }

}
