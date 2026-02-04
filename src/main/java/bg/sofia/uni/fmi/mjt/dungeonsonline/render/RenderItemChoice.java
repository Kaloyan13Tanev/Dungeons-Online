package bg.sofia.uni.fmi.mjt.dungeonsonline.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.map.WalkableTile;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Item;

import java.util.List;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class RenderItemChoice {

    private static final int START_COLUMN = 137;
    private static final int START_ROW = 20;
    private static final String LABEL = "=".repeat(29) + " SELECT  ITEM " + "=".repeat(29);
    private static final String COLOR_FIRST_PART = "\033[33m";
    private static final String COLOR_SECOND_PART = "\033[0m";
    private static final RenderItem RENDER_ITEM = new RenderItem();

    private final List<Item> items;
    private final int clearColumns;

    public RenderItemChoice(List<Item> items) {
        this.items = items;
        this.clearColumns = items.size();
    }

    public void render() {
        Console.moveCursor(START_ROW, START_COLUMN);
        terminal.writer().print(LABEL);

        int i = 0;
        for (Item item : items) {
            Console.moveCursor(START_ROW + i + 1, START_COLUMN);
            terminal.writer().print(RENDER_ITEM.render(item));

            i++;
        }

        terminal.writer().flush();
    }

    public void changeHighlights(int prev, int curr, List<Item> items) {
        unhighlightPrevSelection(prev, items);
        highlightSelection(curr, items);
    }

    public void highlightSelection(int i, List<Item> items) {
        Item item = items.get(i);

        Console.moveCursor(START_ROW + i + 1, START_COLUMN);
        terminal.writer().print(COLOR_FIRST_PART + RENDER_ITEM.render(item) + COLOR_SECOND_PART);
    }

    public void unhighlightPrevSelection(int i, List<Item> items) {
        Item item = items.get(i);

        Console.moveCursor(START_ROW + i + 1, START_COLUMN);
        terminal.writer().print(RENDER_ITEM.render(item));
    }

    public void clearSelection() {
        Console.clearArea(START_ROW, START_COLUMN, LABEL.length(), clearColumns + 1);
    }

}
