package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.TerrainKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.HandshakeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameRendererImplTest {

    private static final int MY_ID = 1;
    private static final int OTHER_PLAYER_ID = 2;
    private static final int MINION_ID = 10;
    private static final int TREASURE_ID = 5;

    private static final int ROW = 3;
    private static final int COL = 4;

    private static final ActorDTO OTHER_PLAYER = new ActorDTO(OTHER_PLAYER_ID, ActorKind.PLAYER, ROW, COL);
    private static final ActorDTO MINION = new ActorDTO(MINION_ID, ActorKind.MINION, ROW, COL);
    private static final TreasureDTO TREASURE = new TreasureDTO(TREASURE_ID, null, ROW, COL);

    private static final TerrainDTO TERRAIN = new TerrainDTO(List.of(List.of(TerrainKind.GROUND)));
    private static final GameStateDTO STATE = new GameStateDTO(List.of(MINION), List.of(TREASURE), null);

    private static final String MESSAGE = "A minion died.";
    private static final String ERROR = "You have nothing to drop!";

    @Mock
    private Console console;
    @Mock
    private ClientState state;

    @Mock
    private MapRenderer map;
    @Mock
    private StatsRenderer stats;
    @Mock
    private BackpackRenderer backpack;
    @Mock
    private MessageRenderer messages;
    @Mock
    private SelectionRenderer selection;

    private GameRendererImpl renderer;

    @BeforeEach
    void setUp() {
        renderer = new GameRendererImpl(console, state, map, stats, backpack, messages, selection);
    }

    @Test
    void testRenderHandshakeKeepsThePlayerIdAndTheTerrain() {
        renderer.renderHandshake(new HandshakeResponse(true, MY_ID, null, TERRAIN));

        verify(state).setPlayerId(MY_ID);
        verify(state).setTerrain(TERRAIN);
    }

    @Test
    void testRenderHandshakeDrawsTheMapOnAClearScreen() {
        renderer.renderHandshake(new HandshakeResponse(true, MY_ID, null, TERRAIN));

        verify(console).clearScreen();
        verify(map).render(state);
        verify(messages).render(state);
    }

    @Test
    void testRenderStateKeepsTheNewState() {
        when(state.getMode()).thenReturn(Mode.EXPLORING);

        renderer.renderState(STATE);

        verify(state).setState(STATE);
    }

    @Test
    void testRenderStateDrawsEveryPartOfTheScreen() {
        when(state.getMode()).thenReturn(Mode.EXPLORING);

        renderer.renderState(STATE);

        verify(map).render(state);
        verify(stats).render(state);
        verify(backpack).render(state);
        verify(selection).render(state);
    }

    @Test
    void testRenderEventKeepsTheMessage() {
        renderer.renderEvent(MESSAGE);

        verify(state).addMessage(MESSAGE);
        verify(messages).render(state);
    }

    @Test
    void testRenderErrorKeepsTheMessageAsAnError() {
        renderer.renderError(ERROR);

        verify(state).addError(ERROR);
        verify(messages).render(state);
    }

    @Test
    void testRenderStateLeavesTheSelectionAloneWhileExploring() {
        when(state.getMode()).thenReturn(Mode.EXPLORING);

        renderer.renderState(STATE);

        verify(state, never()).setMode(any());
        verify(state, never()).setHighlightedId(any());
    }

    @Test
    void testRenderStateClosesTheSelectionWhenNothingIsLeftToChoose() {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.actorsOnMyTile()).thenReturn(List.of());

        renderer.renderState(STATE);

        verify(state).setMode(Mode.EXPLORING);
        verify(state).setHighlightedId(null);
    }

    @Test
    void testRenderStateMovesTheHighlightWhenTheChosenOneIsGone() {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.actorsOnMyTile()).thenReturn(List.of(OTHER_PLAYER));
        when(state.getHighlightedId()).thenReturn(MINION_ID);

        renderer.renderState(STATE);

        verify(state).setHighlightedId(OTHER_PLAYER_ID);
    }

    @Test
    void testRenderStateKeepsTheHighlightWhileTheChosenOneIsStillThere() {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.actorsOnMyTile()).thenReturn(List.of(OTHER_PLAYER, MINION));
        when(state.getHighlightedId()).thenReturn(MINION_ID);

        renderer.renderState(STATE);

        verify(state, never()).setHighlightedId(any());
    }

    @Test
    void testRenderStateRevisesTheTreasureSelectionFromTheTreasuresOnTheTile() {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TREASURE);
        when(state.treasuresOnMyTile()).thenReturn(List.of(TREASURE));

        renderer.renderState(STATE);

        verify(state).setHighlightedId(TREASURE_ID);
    }

    @Test
    void testRenderStateRevisesThePlayerSelectionFromThePlayersOnTheTile() {
        when(state.getMode()).thenReturn(Mode.CHOOSING_PLAYER);
        when(state.playersOnMyTile()).thenReturn(List.of(OTHER_PLAYER));

        renderer.renderState(STATE);

        verify(state).setHighlightedId(OTHER_PLAYER_ID);
    }

}
