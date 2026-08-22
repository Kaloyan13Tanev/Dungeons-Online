package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.TerrainKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapRendererTest {

    private static final TerrainKind G = TerrainKind.GROUND;
    private static final TerrainKind O = TerrainKind.OBSTACLE;

    private static final int MY_ID = 1;
    private static final int OTHER_PLAYER_ID = 2;
    private static final int MINION_ID = 10;
    private static final int TREASURE_ID = 5;

    private static final char MINION_SYMBOL = 'M';
    private static final char OBSTACLE_SYMBOL = 'X';
    private static final char ITEM_SYMBOL = '⚔';

    private static final String HIGHLIGHT_ON = "\033[33m";
    private static final String HIGHLIGHT_OFF = "\033[0m";

    private static final TerrainDTO TERRAIN = new TerrainDTO(List.of(
        List.of(G, O),
        List.of(G, G)));

    private static final ItemDTO ITEM = new ItemDTO(ItemKind.WEAPON, "Sword", 1, 20, 0);

    private static final ActorDTO ME = new ActorDTO(MY_ID, ActorKind.PLAYER, 0, 0);
    private static final ActorDTO OTHER_PLAYER = new ActorDTO(OTHER_PLAYER_ID, ActorKind.PLAYER, 0, 0);
    private static final ActorDTO MINION = new ActorDTO(MINION_ID, ActorKind.MINION, 1, 1);
    private static final TreasureDTO TREASURE = new TreasureDTO(TREASURE_ID, ITEM, 1, 0);

    @Mock
    private Console console;
    @Mock
    private ClientState state;
    @Mock
    private ItemFormatter items;

    private MapRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new MapRenderer(console, items);
    }

    @Test
    void testRenderFillsAnObstacleTileWithItsSymbol() {
        mockWorld(List.of(), List.of());

        renderer.render(state);

        assertTrue(printed().stream().anyMatch(line -> line.indexOf(OBSTACLE_SYMBOL) >= 0),
            "MapRenderer should fill an obstacle tile with the obstacle symbol");
    }

    @Test
    void testRenderShowsAMinionByItsSymbol() {
        mockWorld(List.of(MINION), List.of());

        renderer.render(state);

        verify(console).print(String.valueOf(MINION_SYMBOL));
    }

    @Test
    void testRenderHighlightsThePlayerLookingAtTheMap() {
        mockWorld(List.of(ME), List.of());

        renderer.render(state);

        verify(console).print(HIGHLIGHT_ON + MY_ID + HIGHLIGHT_OFF);
    }

    @Test
    void testRenderShowsTheOtherPlayersWithoutAHighlight() {
        mockWorld(List.of(OTHER_PLAYER), List.of());

        renderer.render(state);

        verify(console).print(String.valueOf(OTHER_PLAYER_ID));
        verify(console, never()).print(HIGHLIGHT_ON + OTHER_PLAYER_ID + HIGHLIGHT_OFF);
    }

    @Test
    void testRenderShowsATreasureByTheSymbolOfItsItem() {
        mockWorld(List.of(), List.of(TREASURE));
        when(items.symbol(ITEM)).thenReturn(ITEM_SYMBOL);

        renderer.render(state);

        verify(console).print(String.valueOf(ITEM_SYMBOL));
    }

    @Test
    void testRenderClearsAGroundTile() {
        mockWorld(List.of(), List.of());

        renderer.render(state);

        verify(console, atLeastOnce()).clearArea(anyInt(), anyInt(), anyInt(), anyInt());
        verify(console).flush();
    }

    private void mockWorld(List<ActorDTO> actors, List<TreasureDTO> treasures) {
        when(state.getTerrain()).thenReturn(TERRAIN);
        when(state.getState()).thenReturn(new GameStateDTO(actors, treasures, null));
        when(state.getPlayerId()).thenReturn(MY_ID);
    }

    private List<String> printed() {
        ArgumentCaptor<String> lines = ArgumentCaptor.forClass(String.class);
        verify(console, atLeastOnce()).print(lines.capture());

        return lines.getAllValues();
    }

}
