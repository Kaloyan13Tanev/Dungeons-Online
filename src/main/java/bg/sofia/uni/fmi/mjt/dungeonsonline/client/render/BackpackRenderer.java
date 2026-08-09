package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;

import java.util.List;

public class BackpackRenderer implements Renderer {

    private static final int START_ROW = 5;
    private static final String LABEL = "=".repeat(31) + " BACKPACK " + "=".repeat(31);
    private static final String SELECTED_MARK = "> ";
    private static final String UNSELECTED_MARK = "  ";

    private final Console console;
    private final ItemFormatter items;
    private final int startColumn;

    public BackpackRenderer(Console console, ItemFormatter items, int startColumn) {
        this.console = console;
        this.items = items;
        this.startColumn = startColumn;
    }

    @Override
    public void render(ClientState state) {
        if (state.getState() == null) {
            return;
        }

        List<ItemDTO> backpack = state.getState().player().backpack();
        int selected = state.getState().player().selectedSlot();

        console.clearArea(START_ROW, startColumn, LABEL.length(), backpack.size() + 1);

        console.moveCursor(START_ROW, startColumn);
        console.print(LABEL);

        for (int slot = 0; slot < backpack.size(); slot++) {
            console.moveCursor(START_ROW + slot + 1, startColumn);
            console.print(slot == selected ? SELECTED_MARK : UNSELECTED_MARK);
            console.print(items.format(backpack.get(slot)));
        }

        console.flush();
    }

}
