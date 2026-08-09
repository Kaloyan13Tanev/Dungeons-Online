package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;

import java.util.ArrayList;
import java.util.List;

public class SelectionRenderer implements Renderer {

    private static final int START_ROW = 25;
    private static final int MAX_LINES = 11;

    private static final String ITEM_LABEL = "=".repeat(29) + " SELECT  ITEM " + "=".repeat(29);
    private static final String ENTITY_LABEL = "=".repeat(28) + " SELECT  ENTITY " + "=".repeat(28);

    private static final String HIGHLIGHT_ON = "\033[33m";
    private static final String HIGHLIGHT_OFF = "\033[0m";

    private final Console console;
    private final ItemFormatter items;
    private final int startColumn;

    public SelectionRenderer(Console console, ItemFormatter items, int startColumn) {
        this.console = console;
        this.items = items;
        this.startColumn = startColumn;
    }

    @Override
    public void render(ClientState state) {
        console.clearArea(START_ROW, startColumn, ENTITY_LABEL.length(), MAX_LINES);

        if (state.getMode() == Mode.EXPLORING) {
            console.flush();
            return;
        }

        List<Entry> entries = toEntries(state);

        console.moveCursor(START_ROW, startColumn);
        console.print(state.getMode() == Mode.CHOOSING_TREASURE ? ITEM_LABEL : ENTITY_LABEL);

        for (int line = 0; line < entries.size(); line++) {
            Entry entry = entries.get(line);
            boolean highlighted = state.getHighlightedId() != null && state.getHighlightedId() == entry.id();

            console.moveCursor(START_ROW + line + 1, startColumn);
            console.print(highlighted ? HIGHLIGHT_ON + entry.text() + HIGHLIGHT_OFF : entry.text());
        }

        console.flush();
    }

    private List<Entry> toEntries(ClientState state) {
        List<Entry> entries = new ArrayList<>();

        if (state.getMode() == Mode.CHOOSING_TREASURE) {
            for (TreasureDTO treasure : state.treasuresOnMyTile()) {
                entries.add(new Entry(treasure.id(), items.format(treasure.item())));
            }

            return entries;
        }

        List<ActorDTO> actors = state.getMode() == Mode.CHOOSING_PLAYER
            ? state.playersOnMyTile()
            : state.actorsOnMyTile();

        for (ActorDTO actor : actors) {
            entries.add(new Entry(actor.id(), items.format(actor)));
        }

        return entries;
    }

    private record Entry(int id, String text) { }

}
