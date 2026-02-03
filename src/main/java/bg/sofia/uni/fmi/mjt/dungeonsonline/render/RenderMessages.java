package bg.sofia.uni.fmi.mjt.dungeonsonline.render;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class RenderMessages {

    private static final int START_COLUMN = 137;
    private static final int START_ROW = 17;
    private static final String LABEL = "=".repeat(31) + " MESSAGES " + "=".repeat(31);

    public void render() {
        Console.moveCursor(START_ROW, START_COLUMN);
        terminal.writer().print(LABEL);

        renderMessage("Here you will receive error messages from the game.");
    }

    public void renderMessage(String message) {
        Console.moveCursor(START_ROW + 1, START_COLUMN);
        terminal.writer().print(message);
        terminal.writer().flush();
    }

}
