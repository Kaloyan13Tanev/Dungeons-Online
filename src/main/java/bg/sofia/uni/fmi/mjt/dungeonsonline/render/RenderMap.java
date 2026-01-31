package bg.sofia.uni.fmi.mjt.dungeonsonline.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.map.GameMap;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class RenderMap {

    private static final GameMap GAME_MAP = new GameMap();
    private static final int BORDER_WIDTH = 13;
    private static final int BORDER_HEIGHT = 6;
    private static final String HORIZONTAL_BORDER =
            ("+" + "-".repeat(BORDER_WIDTH - 2)).repeat(GAME_MAP.getWidth()) + "+";
    private static final char VERTICAL_BORDER_SYMBOL = '|';

    public RenderMap() {

    }

    public void render() {
        terminal.enterRawMode();
        Console.clearScreen();

        int row = 1;

        for (int i = 0; i < GAME_MAP.getHeight(); i++) {
            int col = 1;

            Console.moveCursor(row, col);
            terminal.writer().print(HORIZONTAL_BORDER);

            for (int j = 0; j < GAME_MAP.getWidth(); j++) {
                renderSingleVerticalBorder(row, col);

                RenderTile.render(GAME_MAP.getTile(i, j), row + 1, col + 1);

                col += BORDER_WIDTH - 1;
            }

            renderSingleVerticalBorder(row, col);

            row += BORDER_HEIGHT - 1;
        }

        Console.moveCursor(row, 1);
        terminal.writer().print(HORIZONTAL_BORDER);

        terminal.writer().flush();
    }

    private void renderSingleVerticalBorder(int row, int col) {
        for (int i = 0; i < BORDER_HEIGHT - 2; i++) {
            Console.moveCursor(row + i + 1, col);
            terminal.writer().print(VERTICAL_BORDER_SYMBOL);
        }
    }
}