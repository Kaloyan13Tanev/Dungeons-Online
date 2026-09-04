package bg.sofia.uni.fmi.mjt.dungeonsonline.client.input;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KeyReaderTest {

    private static final int ESCAPE_BYTE = 27;
    private static final int BRACKET_BYTE = 91;
    private static final int SS3_BYTE = 79;

    private static final int UP_BYTE = 65;

    private static final int RETURN_BYTE = 13;
    private static final int NEWLINE_BYTE = 10;

    private static final int NOTHING = -2;
    private static final int EXHAUSTED = -1;

    private static final int UPPERCASE_KEY = 'W';
    private static final int LOWERCASE_KEY = 'w';
    private static final int UNKNOWN_BYTE = 'x';

    @Mock
    private Console console;

    private KeyReader reader;

    @BeforeEach
    void setUp() {
        reader = new KeyReader(console);
    }

    @Test
    void testEnterRawModeCallsTheConsole() {
        reader.enterRawMode();

        verify(console).enterRawMode();
    }

    @Test
    void testReadReturnsEndOfInputWhenTheConsoleIsExhausted() throws IOException {
        when(console.read()).thenReturn(EXHAUSTED);

        assertEquals(KeyReader.END_OF_INPUT, reader.read(),
            "KeyReader should report the end of the input when the console has nothing left to give");
    }

    @Test
    void testReadReturnsLetterLowerCase() throws IOException {
        when(console.read()).thenReturn(UPPERCASE_KEY);

        assertEquals(LOWERCASE_KEY, reader.read(),
            "KeyReader should lowercase a letter key so that both cases mean the same");
    }

    @Test
    void testReadReturnsEnterForBothLineEndings() throws IOException {
        when(console.read()).thenReturn(RETURN_BYTE, NEWLINE_BYTE);

        assertEquals(KeyReader.ENTER, reader.read(),
            "KeyReader should read a carriage return as Enter");
        assertEquals(KeyReader.ENTER, reader.read(),
            "KeyReader should read a newline as Enter");
    }

    @Test
    void testReadReturnsEscapeWhenNothingFollowsIt() throws IOException {
        when(console.read()).thenReturn(ESCAPE_BYTE);
        when(console.read(anyLong())).thenReturn(NOTHING);

        assertEquals(KeyReader.ESCAPE, reader.read(),
            "KeyReader should read a lone escape byte as the escape key");
    }

    @Test
    void testReadReturnsTheArrowOfAnEscapeSequence() throws IOException {
        when(console.read()).thenReturn(ESCAPE_BYTE);
        when(console.read(anyLong())).thenReturn(BRACKET_BYTE, UP_BYTE);

        assertEquals(KeyReader.ARROW_UP, reader.read(),
            "KeyReader should read the arrow an escape sequence ends with");
    }

    @Test
    void testReadReturnsTheArrowOfAnApplicationModeSequence() throws IOException {
        when(console.read()).thenReturn(ESCAPE_BYTE);
        when(console.read(anyLong())).thenReturn(SS3_BYTE, UP_BYTE);

        assertEquals(KeyReader.ARROW_UP, reader.read(),
            "KeyReader should read an arrow that a terminal in application mode sends");
    }

    @Test
    void testReadReturnsUnboundWhenTheEscapeStartsNoSequence() throws IOException {
        when(console.read()).thenReturn(ESCAPE_BYTE);
        when(console.read(anyLong())).thenReturn(UNKNOWN_BYTE);

        assertEquals(KeyReader.UNBOUND, reader.read(),
            "KeyReader should leave an escape byte that starts no known sequence unbound");
    }

    @Test
    void testReadReturnsUnboundWhenTheSequenceIsNotAnArrow() throws IOException {
        when(console.read()).thenReturn(ESCAPE_BYTE);
        when(console.read(anyLong())).thenReturn(BRACKET_BYTE, UNKNOWN_BYTE);

        assertEquals(KeyReader.UNBOUND, reader.read(),
            "KeyReader should leave an escape sequence that is not an arrow unbound");
    }

}
