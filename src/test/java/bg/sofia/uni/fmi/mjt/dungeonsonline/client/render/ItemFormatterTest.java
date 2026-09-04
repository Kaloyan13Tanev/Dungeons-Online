package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.actor.ActorView;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.actor.ActorViewRouter;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.item.ItemView;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.item.ItemViewRouter;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemFormatterTest {

    private static final int PLAYER_ID = 1;

    private static final int ROW = 3;
    private static final int COL = 4;

    private static final int LEVEL = 2;
    private static final int POWER = 40;
    private static final int NO_MANA_COST = 0;

    private static final char SYMBOL = 'W';

    private static final String WEAPON_NAME = "Sword";
    private static final String EMPTY_SLOT = "[EMPTY]";

    private static final ItemDTO WEAPON =
        new ItemDTO(ItemKind.WEAPON, WEAPON_NAME, LEVEL, POWER, NO_MANA_COST);

    private static final ActorDTO PLAYER = new ActorDTO(PLAYER_ID, ActorKind.PLAYER, ROW, COL);

    @Mock
    private ItemViewRouter views;
    @Mock
    private ActorViewRouter actors;

    @Mock
    private ItemView itemView;
    @Mock
    private ActorView actorView;

    private ItemFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new ItemFormatter(views, actors, EMPTY_SLOT);
    }

    @Test
    void testFormatShowsTheViewItsRouterMadeOfTheItem() {
        when(views.route(WEAPON)).thenReturn(itemView);

        assertEquals("[" + itemView + "]", formatter.format(WEAPON),
            "ItemFormatter should show the view its router made of the item between brackets");
    }

    @Test
    void testFormatShowsAnEmptySlotWhenThereIsNoItem() {
        assertEquals(EMPTY_SLOT, formatter.format((ItemDTO) null),
            "ItemFormatter should show an empty slot when there is no item");
    }

    @Test
    void testSymbolTakesTheSymbolOfTheViewItsRouterMadeOfTheItem() {
        when(views.route(WEAPON)).thenReturn(itemView);
        when(itemView.symbol()).thenReturn(SYMBOL);

        assertEquals(SYMBOL, formatter.symbol(WEAPON),
            "ItemFormatter should take the symbol of an item from the view its router made");
    }

    @Test
    void testFormatShowsTheViewItsRouterMadeOfTheActor() {
        when(actors.route(PLAYER)).thenReturn(actorView);

        assertEquals("[" + actorView + "]", formatter.format(PLAYER),
            "ItemFormatter should show the view its router made of the actor between brackets");
    }

}
