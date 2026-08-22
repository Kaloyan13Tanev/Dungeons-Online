package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
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
public class TreasureMapperTest {

    private static final int TREASURE_ID = 5;
    private static final int SECOND_TREASURE_ID = 6;

    private static final int ROW = 3;
    private static final int COL = 4;

    private static final Position POSITION = new Position(ROW, COL);

    private static final ItemDTO ITEM_DTO = new ItemDTO(ItemKind.WEAPON, "Sword", 1, 20, 0);

    @Mock
    private ItemMapper items;

    @Mock
    private Item item;
    @Mock
    private Treasure treasure;
    @Mock
    private Treasure secondTreasure;

    private TreasureMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TreasureMapper(items);
    }

    @Test
    void testToDTOMapsATreasureToItsIdItemAndPosition() {
        when(treasure.getId()).thenReturn(TREASURE_ID);
        when(treasure.getItem()).thenReturn(item);
        when(treasure.getPosition()).thenReturn(POSITION);
        when(items.toDTO(item)).thenReturn(ITEM_DTO);

        assertEquals(new TreasureDTO(TREASURE_ID, ITEM_DTO, ROW, COL), mapper.toDTO(treasure),
            "TreasureMapper should map a treasure to its id, the item its item mapper made and its position");
    }

    @Test
    void testToDTOsMapsEveryTreasureItWasGiven() {
        when(treasure.getId()).thenReturn(TREASURE_ID);
        when(treasure.getPosition()).thenReturn(POSITION);
        when(secondTreasure.getId()).thenReturn(SECOND_TREASURE_ID);
        when(secondTreasure.getPosition()).thenReturn(POSITION);

        assertEquals(List.of(new TreasureDTO(TREASURE_ID, null, ROW, COL),
                new TreasureDTO(SECOND_TREASURE_ID, null, ROW, COL)),
            mapper.toDTOs(List.of(treasure, secondTreasure)),
            "TreasureMapper should map every treasure it was given, in the order they came in");
    }

}
