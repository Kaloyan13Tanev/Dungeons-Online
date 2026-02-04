package bg.sofia.uni.fmi.mjt.dungeonsonline.render;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class Console {

    public Console() {
    }

    public static void moveCursor(int row, int col) {
        terminal.writer().print("\033[" + row + ";" + col + "H");
    }

    public static void clearScreen() {
        terminal.writer().flush();
        terminal.writer().print("\033[2J\033[H");
        terminal.writer().flush();
    }

    public static void clearArea(int startRow, int startCol, int width, int height) {
        String spaces = " ".repeat(width);

        for (int i = 0; i < height; i++) {
            Console.moveCursor(startRow + i, startCol);
            terminal.writer().print(spaces);
        }

        terminal.writer().flush();
    }

}
