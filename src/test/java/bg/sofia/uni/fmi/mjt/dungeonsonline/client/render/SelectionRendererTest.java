package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SelectionRendererTest {

    private static final int START_COLUMN = 1;

    private static final int OTHER_PLAYER_ID = 2;
    private static final int MINION_ID = 10;
    private static final int TREASURE_ID = 5;

    private static final int ROW = 3;
    private static final int COL = 4;

    private static final String HIGHLIGHT_ON = "\033[33m";
    private static final String HIGHLIGHT_OFF = "\033[0m";

    private static final ItemDTO ITEM = new ItemDTO(ItemKind.WEAPON, "Sword", 1, 20, 0);

    private static final ActorDTO OTHER_PLAYER = new ActorDTO(OTHER_PLAYER_ID, ActorKind.PLAYER, ROW, COL);
    private static final ActorDTO MINION = new ActorDTO(MINION_ID, ActorKind.MINION, ROW, COL);
    private static final TreasureDTO TREASURE = new TreasureDTO(TREASURE_ID, ITEM, ROW, COL);

    private static final String PLAYER_TEXT = "[Player 2]";
    private static final String MINION_TEXT = "[Minion]";
    private static final String ITEM_TEXT = "[Sword]";

    @Mock
    private Console console;
    @Mock
    private ClientState state;
    @Mock
    private ItemFormatter items;

    private SelectionRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new SelectionRenderer(console, items, START_COLUMN);
    }

    @Test
    void testRenderShowsNothingWhileExploring() {
        when(state.getMode()).thenReturn(Mode.EXPLORING);

        renderer.render(state);

        verify(console).clearArea(anyInt(), anyInt(), anyInt(), anyInt());
        verify(console, never()).print(anyString());
    }

    @Test
    void testRenderShowsEveryActorOnTheTile() {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.actorsOnMyTile()).thenReturn(List.of(OTHER_PLAYER, MINION));
        when(items.format(OTHER_PLAYER)).thenReturn(PLAYER_TEXT);
        when(items.format(MINION)).thenReturn(MINION_TEXT);

        renderer.render(state);

        verify(console).print(PLAYER_TEXT);
        verify(console).print(MINION_TEXT);
        verify(console, never()).print(startsWith(HIGHLIGHT_ON));
    }

    @Test
    void testRenderHighlightsTheChosenEntry() {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.actorsOnMyTile()).thenReturn(List.of(OTHER_PLAYER, MINION));
        when(state.getHighlightedId()).thenReturn(MINION_ID);
        when(items.format(OTHER_PLAYER)).thenReturn(PLAYER_TEXT);
        when(items.format(MINION)).thenReturn(MINION_TEXT);

        renderer.render(state);

        verify(console).print(HIGHLIGHT_ON + MINION_TEXT + HIGHLIGHT_OFF);
        verify(console).print(PLAYER_TEXT);
    }

    @Test
    void testRenderShowsTheItemsOfTheTreasuresWhileChoosingATreasure() {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TREASURE);
        when(state.treasuresOnMyTile()).thenReturn(List.of(TREASURE));
        when(items.format(ITEM)).thenReturn(ITEM_TEXT);

        renderer.render(state);

        verify(console).print(ITEM_TEXT);
    }

    @Test
    void testRenderShowsOnlyThePlayersWhileChoosingAPlayer() {
        when(state.getMode()).thenReturn(Mode.CHOOSING_PLAYER);
        when(state.playersOnMyTile()).thenReturn(List.of(OTHER_PLAYER));
        when(items.format(OTHER_PLAYER)).thenReturn(PLAYER_TEXT);

        renderer.render(state);

        verify(console).print(PLAYER_TEXT);
        verify(state).playersOnMyTile();
    }

}
