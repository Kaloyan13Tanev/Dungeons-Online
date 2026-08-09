package bg.sofia.uni.fmi.mjt.dungeonsonline.client.console;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JLineConsole implements Console {

    private static final Logger LOGGER = Logger.getLogger(JLineConsole.class.getName());
    private static final String CLEAR_SCREEN = "\033[2J\033[3J\033[H";

    private final Terminal terminal;

    public JLineConsole() throws IOException {
        this.terminal = TerminalBuilder.builder().system(true).build();
    }

    @Override
    public void print(String text) {
        terminal.writer().write(text);
    }

    @Override
    public void moveCursor(int row, int col) {
        terminal.writer().print("\033[" + row + ";" + col + "H");
    }

    @Override
    public void clearScreen() {
        terminal.writer().print(CLEAR_SCREEN);
        terminal.writer().flush();
    }

    @Override
    public void clearArea(int startRow, int startCol, int width, int height) {
        String blanks = " ".repeat(width);

        for (int row = 0; row < height; row++) {
            moveCursor(startRow + row, startCol);
            terminal.writer().print(blanks);
        }
    }

    @Override
    public void flush() {
        terminal.writer().flush();
    }

    @Override
    public int read() throws IOException {
        return terminal.reader().read();
    }

    @Override
    public int read(long timeout) throws IOException {
        return terminal.reader().read(timeout);
    }

    @Override
    public void enterRawMode() {
        terminal.enterRawMode();
    }

    @Override
    public void close() {
        try {
            terminal.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to close the terminal.", e);
        }
    }

}
