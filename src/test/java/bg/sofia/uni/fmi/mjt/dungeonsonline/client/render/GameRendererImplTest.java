package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameRendererImplTest {

    private static final int MY_ID = 1;
    private static final int MINION_ID = 10;
    private static final int TREASURE_ID = 5;

    private static final int ROW = 3;
    private static final int COL = 4;

    private static final ActorDTO MINION = new ActorDTO(MINION_ID, ActorKind.MINION, ROW, COL);
    private static final TreasureDTO TREASURE = new TreasureDTO(TREASURE_ID, null, ROW, COL);

    private static final TerrainDTO TERRAIN = new TerrainDTO(List.of(List.of(TerrainKind.GROUND)));
    private static final GameStateDTO STATE = new GameStateDTO(List.of(MINION), List.of(TREASURE), null);

    private static final HandshakeResponse HANDSHAKE = new HandshakeResponse(true, MY_ID, null, TERRAIN);

    private static final String MESSAGE = "A minion died.";
    private static final String ERROR = "You have nothing to drop!";

    @Mock
    private Console console;
    @Mock
    private ClientState state;
    @Mock
    private Selection selection;

    @Mock
    private MapRenderer map;
    @Mock
    private StatsRenderer stats;
    @Mock
    private BackpackRenderer backpack;
    @Mock
    private MessageRenderer messages;
    @Mock
    private SelectionRenderer selectionRenderer;

    private GameRendererImpl renderer;

    @BeforeEach
    void setUp() {
        renderer = new GameRendererImpl(console, state, selection, map, stats, backpack, messages,
            selectionRenderer);
    }

    @Test
    void testRenderHandshakeSavesThePlayerIdAndTheTerrain() {
        renderer.renderHandshake(HANDSHAKE);

        verify(state).setPlayerId(MY_ID);
        verify(state).setTerrain(TERRAIN);
    }

    @Test
    void testRenderHandshakeDrawsTheMapOnAClearScreen() {
        renderer.renderHandshake(HANDSHAKE);

        verify(console).clearScreen();
        verify(map).render(state);
        verify(messages).render(state);
    }

    @Test
    void testRenderStateKeepsTheNewState() {
        renderer.renderState(STATE);

        verify(state).setState(STATE);
    }

    @Test
    void testRenderStateDrawsEveryPartOfTheScreen() {
        renderer.renderState(STATE);

        verify(map).render(state);
        verify(stats).render(state);
        verify(backpack).render(state);
        verify(selectionRenderer).render(state);
    }

    @Test
    void testRenderStateRevisesTheSelectionBeforeItDrawsIt() {
        renderer.renderState(STATE);

        InOrder inOrder = inOrder(selection, selectionRenderer);
        inOrder.verify(selection).revise();
        inOrder.verify(selectionRenderer).render(state);
    }

    @Test
    void testRenderEventSavesTheMessage() {
        renderer.renderEvent(MESSAGE);

        verify(state).addMessage(MESSAGE);
        verify(messages).render(state);
    }

    @Test
    void testRenderErrorSavesTheMessageAsAnError() {
        renderer.renderError(ERROR);

        verify(state).addError(ERROR);
        verify(messages).render(state);
    }

    @Test
    void testRenderSelectionDrawsTheSelection() {
        renderer.renderSelection();

        verify(selectionRenderer).render(state);
    }

}
