package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Message;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageRendererTest {

    private static final int START_ROW = 17;
    private static final int START_COLUMN = 1;

    private static final String ERROR_ON = "\033[31m";
    private static final String COLOR_OFF = "\033[0m";

    private static final String FIRST_TEXT = "A minion died.";
    private static final String SECOND_TEXT = "You have nothing to drop!";

    private static final Message MESSAGE = new Message(FIRST_TEXT, false);
    private static final Message ERROR = new Message(SECOND_TEXT, true);

    @Mock
    private Console console;
    @Mock
    private ClientState state;

    private MessageRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new MessageRenderer(console, START_COLUMN);
    }

    @Test
    void testRenderPrintsEveryMessageOfTheState() {
        when(state.getMessages()).thenReturn(List.of(MESSAGE, ERROR));

        renderer.render(state);

        verify(console).print(FIRST_TEXT);
    }

    @Test
    void testRenderPrintsEveryMessageOnItsOwnLine() {
        when(state.getMessages()).thenReturn(List.of(MESSAGE, ERROR));

        renderer.render(state);

        verify(console).moveCursor(START_ROW + 1, START_COLUMN);
        verify(console).moveCursor(START_ROW + 2, START_COLUMN);
    }

    @Test
    void testRenderColoursAnErrorInRed() {
        when(state.getMessages()).thenReturn(List.of(ERROR));

        renderer.render(state);

        verify(console).print(ERROR_ON + SECOND_TEXT + COLOR_OFF);
    }

    @Test
    void testRenderClearsItsAreaBeforeItPrints() {
        when(state.getMessages()).thenReturn(List.of(MESSAGE));

        renderer.render(state);

        verify(console).clearArea(anyInt(), anyInt(), anyInt(), anyInt());
        verify(console).flush();
    }

    @Test
    void testRenderPrintsTheLabelOfTheMessages() {
        when(state.getMessages()).thenReturn(List.of());

        renderer.render(state);

        verify(console).print(anyString());
    }

}
