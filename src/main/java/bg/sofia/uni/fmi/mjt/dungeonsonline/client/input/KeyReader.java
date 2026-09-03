package bg.sofia.uni.fmi.mjt.dungeonsonline.client.input;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;

import java.io.IOException;
import java.util.Map;

public class KeyReader {

    public static final int END_OF_INPUT = -1;
    public static final int UNBOUND = -3;

    public static final int ARROW_UP = 1000;
    public static final int ARROW_DOWN = 1001;
    public static final int ARROW_RIGHT = 1002;
    public static final int ARROW_LEFT = 1003;
    public static final int ESCAPE = 1004;
    public static final int ENTER = 1005;

    private static final int ESCAPE_BYTE = 27;
    private static final int BRACKET_BYTE = 91;
    private static final int SS3_BYTE = 79;
    private static final int RETURN_BYTE = 13;
    private static final int NEWLINE_BYTE = 10;
    private static final int NOTHING = -2;

    private static final long ESCAPE_TIMEOUT = 50;

    private static final Map<Integer, Integer> ARROWS = Map.of(
        65, ARROW_UP,
        66, ARROW_DOWN,
        67, ARROW_RIGHT,
        68, ARROW_LEFT
    );

    private final Console console;

    public KeyReader(Console console) {
        this.console = console;
    }

    public void enterRawMode() {
        console.enterRawMode();
    }

    public int read() throws IOException {
        int key = console.read();

        if (key < 0) {
            return END_OF_INPUT;
        }

        if (key == ESCAPE_BYTE) {
            return readEscape();
        }

        if (key == RETURN_BYTE || key == NEWLINE_BYTE) {
            return ENTER;
        }

        return Character.toLowerCase(key);
    }

    private int readEscape() throws IOException {
        int next = console.read(ESCAPE_TIMEOUT);

        if (next == NOTHING) {
            return ESCAPE;
        }

        if (next != BRACKET_BYTE && next != SS3_BYTE) {
            return UNBOUND;
        }

        return ARROWS.getOrDefault(console.read(ESCAPE_TIMEOUT), UNBOUND);
    }

}
