package bg.sofia.uni.fmi.mjt.dungeonsonline.client.input;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.RequestSender;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.DropRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.GiveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.PickUpRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.QuitRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.SelectRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.UseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InputLoopTest {

    private static final int ESCAPE = 27;
    private static final int BRACKET = 91;
    private static final int SS3 = 79;
    private static final int ARROW_UP = 65;
    private static final int ARROW_DOWN = 66;
    private static final int ARROW_RIGHT = 67;
    private static final int ARROW_LEFT = 68;
    private static final int ENTER = 13;

    private static final int NOTHING = -2;
    private static final int END_OF_INPUT = -1;

    private static final int UNKNOWN_KEY = 'x';

    private static final int UP_KEY = 'w';
    private static final int LEFT_KEY = 'a';
    private static final int DOWN_KEY = 's';
    private static final int RIGHT_KEY = 'd';
    private static final int UPPERCASE_UP_KEY = 'W';
    private static final int DROP_KEY = 'r';
    private static final int USE_KEY = 'e';
    private static final int TREASURE_KEY = 'f';
    private static final int GIVE_KEY = 'g';
    private static final int CANCEL_KEY = 'q';

    private static final int FIRST_SLOT_KEY = '1';
    private static final int NINTH_SLOT_KEY = '9';
    private static final int LAST_SLOT_KEY = '0';

    private static final int FIRST_SLOT = 0;
    private static final int NINTH_SLOT = 8;
    private static final int LAST_SLOT = 9;

    private static final int MY_ID = 1;
    private static final int OTHER_PLAYER_ID = 2;
    private static final int MINION_ID = 10;
    private static final int TREASURE_ID = 5;

    private static final int ROW = 3;
    private static final int COL = 4;

    private static final ActorDTO OTHER_PLAYER = new ActorDTO(OTHER_PLAYER_ID, ActorKind.PLAYER, ROW, COL);
    private static final ActorDTO MINION = new ActorDTO(MINION_ID, ActorKind.MINION, ROW, COL);
    private static final TreasureDTO TREASURE = new TreasureDTO(TREASURE_ID, null, ROW, COL);

    @Mock
    private Console console;
    @Mock
    private ClientState state;
    @Mock
    private GameRenderer renderer;
    @Mock
    private RequestSender sender;

    private InputLoop loop;

    @BeforeEach
    void setUp() {
        loop = new InputLoop(console, state, renderer, sender);
    }

    @Test
    void testRunEntersRawModeBeforeItReads() throws IOException {
        when(console.read()).thenReturn(END_OF_INPUT);

        loop.run();

        InOrder inOrder = inOrder(console);
        inOrder.verify(console).enterRawMode();
        inOrder.verify(console).read();
    }

    @Test
    void testRunReadsUntilTheInputEnds() throws IOException {
        when(console.read()).thenReturn(UNKNOWN_KEY, UNKNOWN_KEY, END_OF_INPUT);

        loop.run();

        verify(console, times(3)).read();
    }

    @Test
    void testRunSendsQuitRequest() throws IOException {
        when(console.read()).thenReturn(ESCAPE, END_OF_INPUT);
        when(console.read(anyLong())).thenReturn(NOTHING);

        loop.run();

        verify(sender).send(new QuitRequest());
    }

    @Test
    void testMoveKeysSendMoveRequest() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(console.read()).thenReturn(UP_KEY, LEFT_KEY, DOWN_KEY, RIGHT_KEY, END_OF_INPUT);

        loop.run();

        verify(sender).send(new MoveRequest(Direction.UP));
        verify(sender).send(new MoveRequest(Direction.LEFT));
        verify(sender).send(new MoveRequest(Direction.DOWN));
        verify(sender).send(new MoveRequest(Direction.RIGHT));
    }

    @Test
    void testUppercaseMoveKeySendsMoveRequest() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(console.read()).thenReturn(UPPERCASE_UP_KEY, END_OF_INPUT);

        loop.run();

        verify(sender).send(new MoveRequest(Direction.UP));
    }

    @Test
    void testDropKeySendsDropRequest() throws IOException {
        when(console.read()).thenReturn(DROP_KEY, END_OF_INPUT);

        loop.run();

        verify(sender).send(new DropRequest());
    }

    @Test
    void testDigitKeySendsSelectRequest() throws IOException {
        when(console.read()).thenReturn(FIRST_SLOT_KEY, NINTH_SLOT_KEY, END_OF_INPUT);

        loop.run();

        verify(sender).send(new SelectRequest(FIRST_SLOT));
        verify(sender).send(new SelectRequest(NINTH_SLOT));
    }

    @Test
    void testZeroKeySendsSelectRequestForTheLastSlot() throws IOException {
        when(console.read()).thenReturn(LAST_SLOT_KEY, END_OF_INPUT);

        loop.run();

        verify(sender).send(new SelectRequest(LAST_SLOT));
    }

    @Test
    void testUnknownKeySendsNoRequest() throws IOException {
        when(console.read()).thenReturn(UNKNOWN_KEY, END_OF_INPUT);

        loop.run();

        verifyNoInteractions(sender);
    }

    @Test
    void testMoveKeySendsNoRequestWhileChoosing() throws IOException {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(console.read()).thenReturn(UP_KEY, END_OF_INPUT);

        loop.run();

        verifyNoInteractions(sender);
    }

    @Test
    void testArrowKeysSendMoveRequest() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(console.read()).thenReturn(ESCAPE, ESCAPE, ESCAPE, ESCAPE, END_OF_INPUT);
        when(console.read(anyLong())).thenReturn(BRACKET, ARROW_UP, BRACKET, ARROW_DOWN,
            BRACKET, ARROW_LEFT, BRACKET, ARROW_RIGHT);

        loop.run();

        verify(sender).send(new MoveRequest(Direction.UP));
        verify(sender).send(new MoveRequest(Direction.DOWN));
        verify(sender).send(new MoveRequest(Direction.LEFT));
        verify(sender).send(new MoveRequest(Direction.RIGHT));
    }

    @Test
    void testArrowKeyInApplicationModeSendsMoveRequest() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(console.read()).thenReturn(ESCAPE, END_OF_INPUT);
        when(console.read(anyLong())).thenReturn(SS3, ARROW_UP);

        loop.run();

        verify(sender).send(new MoveRequest(Direction.UP));
    }

    @Test
    void testUnknownEscapeSequenceSendsNoRequest() throws IOException {
        when(console.read()).thenReturn(ESCAPE, END_OF_INPUT);
        when(console.read(anyLong())).thenReturn(UNKNOWN_KEY);

        loop.run();

        verifyNoInteractions(sender);
    }

    @Test
    void testUseKeySendsUseRequestWithoutATargetWhenTheTileIsEmpty() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(state.actorsOnMyTile()).thenReturn(List.of());
        when(console.read()).thenReturn(USE_KEY, END_OF_INPUT);

        loop.run();

        verify(sender).send(new UseRequest(null));
    }

    @Test
    void testUseKeyOpensTheTargetSelectionWhenSomebodyIsOnTheTile() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(state.actorsOnMyTile()).thenReturn(List.of(MINION));
        when(console.read()).thenReturn(USE_KEY, END_OF_INPUT);

        loop.run();

        verify(state).setMode(Mode.CHOOSING_TARGET);
        verify(state).setHighlightedId(MINION_ID);
        verify(renderer).renderSelection();
        verifyNoInteractions(sender);
    }

    @Test
    void testTreasureKeyOpensTheTreasureSelection() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(state.treasuresOnMyTile()).thenReturn(List.of(TREASURE));
        when(console.read()).thenReturn(TREASURE_KEY, END_OF_INPUT);

        loop.run();

        verify(state).setMode(Mode.CHOOSING_TREASURE);
        verify(state).setHighlightedId(TREASURE_ID);
        verify(renderer).renderSelection();
    }

    @Test
    void testGiveKeyOpensThePlayerSelection() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(state.playersOnMyTile()).thenReturn(List.of(OTHER_PLAYER));
        when(console.read()).thenReturn(GIVE_KEY, END_OF_INPUT);

        loop.run();

        verify(state).setMode(Mode.CHOOSING_PLAYER);
        verify(state).setHighlightedId(OTHER_PLAYER_ID);
        verify(renderer).renderSelection();
    }

    @Test
    void testTreasureKeyRendersAnErrorWhenThereIsNothingToChoose() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(state.treasuresOnMyTile()).thenReturn(List.of());
        when(console.read()).thenReturn(TREASURE_KEY, END_OF_INPUT);

        loop.run();

        verify(renderer).renderError(anyString());
        verify(state, never()).setMode(any());
    }

    @Test
    void testEnterSendsUseRequestForTheHighlightedTarget() throws IOException {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.getHighlightedId()).thenReturn(MINION_ID);
        when(console.read()).thenReturn(ENTER, END_OF_INPUT);

        loop.run();

        verify(sender).send(new UseRequest(MINION_ID));
    }

    @Test
    void testEnterSendsPickUpRequestForTheHighlightedTreasure() throws IOException {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TREASURE);
        when(state.getHighlightedId()).thenReturn(TREASURE_ID);
        when(console.read()).thenReturn(ENTER, END_OF_INPUT);

        loop.run();

        verify(sender).send(new PickUpRequest(TREASURE_ID));
    }

    @Test
    void testEnterSendsGiveRequestForTheHighlightedPlayer() throws IOException {
        when(state.getMode()).thenReturn(Mode.CHOOSING_PLAYER);
        when(state.getHighlightedId()).thenReturn(OTHER_PLAYER_ID);
        when(console.read()).thenReturn(ENTER, END_OF_INPUT);

        loop.run();

        verify(sender).send(new GiveRequest(OTHER_PLAYER_ID));
    }

    @Test
    void testEnterClosesTheSelection() throws IOException {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.getHighlightedId()).thenReturn(MINION_ID);
        when(console.read()).thenReturn(ENTER, END_OF_INPUT);

        loop.run();

        verify(state).setMode(Mode.EXPLORING);
        verify(state).setHighlightedId(null);
        verify(renderer).renderSelection();
    }

    @Test
    void testEnterSendsNoRequestWhileExploring() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(console.read()).thenReturn(ENTER, END_OF_INPUT);

        loop.run();

        verifyNoInteractions(sender);
    }

    @Test
    void testCancelKeyClosesTheSelection() throws IOException {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(console.read()).thenReturn(CANCEL_KEY, END_OF_INPUT);

        loop.run();

        verify(state).setMode(Mode.EXPLORING);
        verify(state).setHighlightedId(null);
        verify(renderer).renderSelection();
    }

    @Test
    void testCancelKeyChangesNothingWhileExploring() throws IOException {
        when(state.getMode()).thenReturn(Mode.EXPLORING);
        when(console.read()).thenReturn(CANCEL_KEY, END_OF_INPUT);

        loop.run();

        verify(state, never()).setMode(any());
    }

    @Test
    void testArrowDownHighlightsTheNextCandidate() throws IOException {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.actorsOnMyTile()).thenReturn(List.of(OTHER_PLAYER, MINION));
        when(state.getHighlightedId()).thenReturn(OTHER_PLAYER_ID);
        when(console.read()).thenReturn(ESCAPE, END_OF_INPUT);
        when(console.read(anyLong())).thenReturn(BRACKET, ARROW_DOWN);

        loop.run();

        verify(state).setHighlightedId(MINION_ID);
        verify(renderer).renderSelection();
    }

    @Test
    void testArrowDownWrapsAroundToTheFirstCandidate() throws IOException {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.actorsOnMyTile()).thenReturn(List.of(OTHER_PLAYER, MINION));
        when(state.getHighlightedId()).thenReturn(MINION_ID);
        when(console.read()).thenReturn(ESCAPE, END_OF_INPUT);
        when(console.read(anyLong())).thenReturn(BRACKET, ARROW_DOWN);

        loop.run();

        verify(state).setHighlightedId(OTHER_PLAYER_ID);
    }

    @Test
    void testArrowUpWrapsAroundToTheLastCandidate() throws IOException {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.actorsOnMyTile()).thenReturn(List.of(OTHER_PLAYER, MINION));
        when(state.getHighlightedId()).thenReturn(OTHER_PLAYER_ID);
        when(console.read()).thenReturn(ESCAPE, END_OF_INPUT);
        when(console.read(anyLong())).thenReturn(BRACKET, ARROW_UP);

        loop.run();

        verify(state).setHighlightedId(MINION_ID);
    }

    @Test
    void testArrowClosesTheSelectionWhenNothingIsLeftToChoose() throws IOException {
        when(state.getMode()).thenReturn(Mode.CHOOSING_TARGET);
        when(state.actorsOnMyTile()).thenReturn(List.of());
        when(console.read()).thenReturn(ESCAPE, END_OF_INPUT);
        when(console.read(anyLong())).thenReturn(BRACKET, ARROW_DOWN);

        loop.run();

        verify(state).setMode(Mode.EXPLORING);
        verify(state).setHighlightedId(null);
    }

}
