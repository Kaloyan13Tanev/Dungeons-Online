package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.actor.ActorView;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.actor.ActorViewRouter;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.terrain.TerrainView;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.terrain.TerrainViewRouter;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapRendererTest {

    private static final int MY_ID = 1;
    private static final int MINION_ID = 10;
    private static final int TREASURE_ID = 5;

    private static final int LEVEL = 1;
    private static final int POWER = 20;
    private static final int NO_MANA_COST = 0;

    private static final int TILES_IN_THE_TERRAIN = 4;

    private static final String MY_SYMBOL = "1";
    private static final String MINION_SYMBOL = "M";
    private static final char ITEM_SYMBOL = '⚔';

    private static final String TILE_LINE = "a line of a tile";

    private static final String HIGHLIGHT_ON = "\033[33m";
    private static final String HIGHLIGHT_OFF = "\033[0m";

    private static final TerrainDTO TERRAIN = new TerrainDTO(List.of(
        List.of(TerrainKind.GROUND, TerrainKind.GROUND),
        List.of(TerrainKind.GROUND, TerrainKind.GROUND)));

    private static final ItemDTO ITEM = new ItemDTO(ItemKind.WEAPON, "Sword", LEVEL, POWER, NO_MANA_COST);

    private static final ActorDTO ME = new ActorDTO(MY_ID, ActorKind.PLAYER, 0, 0);
    private static final ActorDTO MINION = new ActorDTO(MINION_ID, ActorKind.MINION, 1, 1);
    private static final TreasureDTO TREASURE = new TreasureDTO(TREASURE_ID, ITEM, 1, 0);

    @Mock
    private Console console;
    @Mock
    private ClientState state;
    @Mock
    private ItemFormatter items;
    @Mock
    private TerrainViewRouter terrains;
    @Mock
    private ActorViewRouter actors;

    @Mock
    private TerrainView terrainView;
    @Mock
    private ActorView actorView;

    private MapRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new MapRenderer(console, items, terrains, actors);
    }

    @Test
    void testRenderPrintsTheLinesTheViewGaveForEveryTile() {
        mockWorld(List.of(), List.of());

        renderer.render(state);

        verify(console, times(TILES_IN_THE_TERRAIN)).print(TILE_LINE);
    }

    @Test
    void testRenderShowsAnActorByTheSymbolOfItsView() {
        mockWorld(List.of(MINION), List.of());
        when(actors.route(MINION)).thenReturn(actorView);
        when(actorView.symbol()).thenReturn(MINION_SYMBOL);

        renderer.render(state);

        verify(console).print(MINION_SYMBOL);
    }

    @Test
    void testRenderHighlightsThePlayerLookingAtTheMap() {
        mockWorld(List.of(ME), List.of());
        when(actors.route(ME)).thenReturn(actorView);
        when(actorView.symbol()).thenReturn(MY_SYMBOL);

        renderer.render(state);

        verify(console).print(HIGHLIGHT_ON + MY_SYMBOL + HIGHLIGHT_OFF);
    }

    @Test
    void testRenderShowsATreasureByTheSymbolOfItsItem() {
        mockWorld(List.of(), List.of(TREASURE));
        when(items.symbol(ITEM)).thenReturn(ITEM_SYMBOL);

        renderer.render(state);

        verify(console).print(String.valueOf(ITEM_SYMBOL));
    }

    @Test
    void testRenderFlushesWhatItDrew() {
        mockWorld(List.of(), List.of());

        renderer.render(state);

        verify(console).flush();
    }

    private void mockWorld(List<ActorDTO> actors, List<TreasureDTO> treasures) {
        when(state.getTerrain()).thenReturn(TERRAIN);
        when(state.getState()).thenReturn(new GameStateDTO(actors, treasures, null));
        when(state.getPlayerId()).thenReturn(MY_ID);
        when(terrains.route(TerrainKind.GROUND)).thenReturn(terrainView);
        when(terrainView.lines(anyInt(), anyInt())).thenReturn(List.of(TILE_LINE));
    }

}
