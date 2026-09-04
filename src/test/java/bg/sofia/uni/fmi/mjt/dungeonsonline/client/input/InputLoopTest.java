package bg.sofia.uni.fmi.mjt.dungeonsonline.client.input;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.RequestSender;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.DropRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InputLoopTest {

    private static final int KEY = 'r';

    private static final int READS_UNTIL_THE_INPUT_ENDS = 3;

    private static final Request REQUEST = new DropRequest();

    @Mock
    private KeyReader reader;
    @Mock
    private ClientState state;
    @Mock
    private Selection selection;
    @Mock
    private KeyBindings bindings;
    @Mock
    private RequestSender sender;

    private InputLoop loop;

    @BeforeEach
    void setUp() {
        loop = new InputLoop(reader, state, selection, bindings, sender);
    }

    @Test
    void testRunEntersRawModeBeforeItReads() throws IOException {
        when(state.isPlaying()).thenReturn(true);
        when(reader.read()).thenReturn(KeyReader.END_OF_INPUT);

        loop.run();

        InOrder inOrder = inOrder(reader);
        inOrder.verify(reader).enterRawMode();
        inOrder.verify(reader).read();
    }

    @Test
    void testRunSendsTheRequestTheKeyIsBoundToInTheCurrentMode() throws IOException {
        when(state.isPlaying()).thenReturn(true);
        when(reader.read()).thenReturn(KEY, KeyReader.END_OF_INPUT);
        when(selection.mode()).thenReturn(Mode.CHOOSING_TARGET);
        when(bindings.press(Mode.CHOOSING_TARGET, KEY)).thenReturn(Optional.of(REQUEST));

        loop.run();

        verify(sender).send(REQUEST);
    }

    @Test
    void testRunReadsUntilTheInputEnds() throws IOException {
        when(state.isPlaying()).thenReturn(true);
        when(reader.read()).thenReturn(KEY, KEY, KeyReader.END_OF_INPUT);

        loop.run();

        verify(reader, times(READS_UNTIL_THE_INPUT_ENDS)).read();
    }

    @Test
    void testRunReadsNoMoreOnceThePlayerHasStoppedPlaying() throws IOException {
        when(state.isPlaying()).thenReturn(true, false);
        when(reader.read()).thenReturn(KEY);

        loop.run();

        verify(reader).read();
    }

}
