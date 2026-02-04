package bg.sofia.uni.fmi.mjt.dungeonsonline.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.attribute.Backpack;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class RenderBackpack {

    private static final int START_COLUMN = 137;
    private static final int START_ROW = 5;
    private static final String LABEL = "=".repeat(31) + " BACKPACK " + "=".repeat(31);

    public void renderBackpack(Backpack backpack) {
        Console.clearArea(START_ROW, START_COLUMN, LABEL.length(), backpack.getSize() + 1);

        RenderItem renderItem = new RenderItem();

        Console.moveCursor(START_ROW, START_COLUMN);
        terminal.writer().print(LABEL);

        for (int i = 0; i < backpack.getSize(); i++) {
            Console.moveCursor(START_ROW + i + 1, START_COLUMN);
            terminal.writer().print(renderItem.render(backpack.get(i)));
        }

        terminal.writer().flush();
    }

}
