package bg.sofia.uni.fmi.mjt.dungeonsonline.client.console;

import org.jline.terminal.Terminal;
import org.jline.utils.NonBlockingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JLineConsoleTest {

    private static final String CLEAR_SCREEN = "\033[2J\033[3J\033[H";

    private static final int ROW = 3;
    private static final int COL = 4;

    private static final int WIDTH = 5;
    private static final int HEIGHT = 2;

    private static final String TEXT = "Player 1 joined the game.";

    private static final long TIMEOUT = 200;

    @Mock
    private Terminal terminal;
    @Mock
    private PrintWriter writer;
    @Mock
    private NonBlockingReader reader;

    private JLineConsole console;

    @BeforeEach
    void setUp() {
        console = new JLineConsole(terminal);
    }

    @Test
    void testMoveCursorPrintsThePositionAsAnEscapeSequence() {
        when(terminal.writer()).thenReturn(writer);

        console.moveCursor(ROW, COL);

        verify(writer).print("\033[" + ROW + ";" + COL + "H");
    }

    @Test
    void testClearScreenPrintsTheClearSequence() {
        when(terminal.writer()).thenReturn(writer);

        console.clearScreen();

        verify(writer).print(CLEAR_SCREEN);
    }

    @Test
    void testClearAreaBlanksAsManyRowsAsItWasGiven() {
        when(terminal.writer()).thenReturn(writer);

        console.clearArea(ROW, COL, WIDTH, HEIGHT);

        verify(writer, times(HEIGHT)).print(" ".repeat(WIDTH));
    }

    @Test
    void testClearAreaBlanksTheRowsFromTheOneItStartsOn() {
        when(terminal.writer()).thenReturn(writer);

        console.clearArea(ROW, COL, WIDTH, HEIGHT);

        verify(writer).print("\033[" + ROW + ";" + COL + "H");
        verify(writer).print("\033[" + (ROW + HEIGHT - 1) + ";" + COL + "H");
    }

    @Test
    void testPrintCallsTerminalWriter() {
        when(terminal.writer()).thenReturn(writer);

        console.print(TEXT);

        verify(writer).write(TEXT);
    }

    @Test
    void testFlushCallsTerminalWriter() {
        when(terminal.writer()).thenReturn(writer);

        console.flush();

        verify(writer).flush();
    }

    @Test
    void testReadCallsTerminalReader() throws IOException {
        when(terminal.reader()).thenReturn(reader);

        console.read();

        verify(reader).read();
    }

    @Test
    void testReadWithATimeoutCallsTerminalReader() throws IOException {
        when(terminal.reader()).thenReturn(reader);

        console.read(TIMEOUT);

        verify(reader).read(TIMEOUT);
    }

    @Test
    void testEnterRawModeCallsTerminal() {
        console.enterRawMode();

        verify(terminal).enterRawMode();
    }

    @Test
    void testCloseCallsTerminal() throws IOException {
        console.close();

        verify(terminal).close();
    }

}
