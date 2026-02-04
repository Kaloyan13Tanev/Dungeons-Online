package bg.sofia.uni.fmi.mjt.dungeonsonline.selector;

import bg.sofia.uni.fmi.mjt.dungeonsonline.render.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderItem;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderItemChoice;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Item;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;

import java.io.IOException;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class ItemSelector {

    private static final BindingReader READER = new BindingReader(terminal.reader());
    private static final KeyMap<String> KEY_MAP = new KeyMap<>();

    private final List<Item> items;
    private final RenderItemChoice itemChoice;

    public ItemSelector(List<Item> items) {
        this.items = items;
        this.itemChoice = new RenderItemChoice(items);
        bindKeys();
    }

    public Item initializeSelector() throws IOException {
        terminal.enterRawMode();

        int selectionIdx = 0;
        itemChoice.highlightSelection(selectionIdx, items);

        while (true) {
            String action = READER.readBinding(KEY_MAP);

            switch (action) {
                case "up" -> {
                    selectionIdx = decreaseSelectionIdx(selectionIdx);
                }
                case "down" -> {
                    selectionIdx = increaseSelectionIdx(selectionIdx);
                }
                case "exit" -> {
                    itemChoice.clearSelection();
                    return null;
                }
                case "enter" -> {
                    itemChoice.clearSelection();
                    return items.get(selectionIdx);
                }
            }
        }
    }

    private int decreaseSelectionIdx(int idx) {
        int newIdx = (idx == 0) ? (items.size() - 1) : (idx - 1);

        return changeSelection(idx, newIdx);
    }

    private int increaseSelectionIdx(int idx) {
        int newIdx = (idx == items.size() - 1) ? 0 : (idx + 1);

        return changeSelection(idx, newIdx);
    }

    private int changeSelection(int prev, int curr) {
        itemChoice.changeHighlights(prev, curr, items);

        return curr;
    }

    private void bindKeys() {
        KEY_MAP.bind("up", KeyMap.key(terminal, org.jline.utils.InfoCmp.Capability.key_up));
        KEY_MAP.bind("down", KeyMap.key(terminal, org.jline.utils.InfoCmp.Capability.key_down));
        KEY_MAP.bind("exit", KeyMap.esc());
        KEY_MAP.bind("enter", "\r");
    }

}