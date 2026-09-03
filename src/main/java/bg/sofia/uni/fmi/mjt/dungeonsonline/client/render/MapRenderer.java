package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.actor.ActorViewRouter;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.terrain.TerrainViewRouter;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MapRenderer implements Renderer {

    private static final int TILE_WIDTH = 11;
    private static final int TILE_HEIGHT = 4;

    private static final int ITEM_ROW = 2;
    private static final int ITEM_ROWS = 2;

    private static final int MINION_ROW = 1;
    private static final int PLAYER_ROW = 0;
    private static final int FIRST_PLAYER_ID = 1;

    private static final char VERTICAL_BORDER = '|';

    private static final String HIGHLIGHT_ON = "\033[33m";
    private static final String HIGHLIGHT_OFF = "\033[0m";

    private static final Map<ActorKind, Function<ActorDTO, Offset>> PLACEMENTS =
        new EnumMap<>(Map.of(
            ActorKind.MINION, actor -> new Offset(MINION_ROW, TILE_WIDTH / 2),
            ActorKind.PLAYER, actor -> new Offset(PLAYER_ROW, actor.id() - FIRST_PLAYER_ID)));

    private final Console console;
    private final ItemFormatter items;
    private final TerrainViewRouter terrains;
    private final ActorViewRouter actors;

    public MapRenderer(Console console, ItemFormatter items, TerrainViewRouter terrains,
                       ActorViewRouter actors) {
        this.console = console;
        this.items = items;
        this.terrains = terrains;
        this.actors = actors;
    }

    @Override
    public void render(ClientState state) {
        TerrainDTO terrain = state.getTerrain();
        if (terrain == null || state.getState() == null) {
            return;
        }

        renderBorders(terrain);
        renderTerrain(terrain);
        renderTreasures(state.getState().treasures());
        renderActors(state.getState().actors(), state.getPlayerId());

        console.flush();
    }

    private void renderBorders(TerrainDTO terrain) {
        int rows = terrain.tiles().size();
        int cols = terrain.tiles().getFirst().size();
        String horizontal = ("+" + "-".repeat(TILE_WIDTH)).repeat(cols) + "+";

        for (int row = 0; row <= rows; row++) {
            console.moveCursor(borderRow(row), 1);
            console.print(horizontal);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col <= cols; col++) {
                for (int line = 0; line < TILE_HEIGHT; line++) {
                    console.moveCursor(terminalRow(row) + line, borderCol(col));
                    console.print(String.valueOf(VERTICAL_BORDER));
                }
            }
        }
    }

    private void renderTerrain(TerrainDTO terrain) {
        for (int row = 0; row < terrain.tiles().size(); row++) {
            for (int col = 0; col < terrain.tiles().get(row).size(); col++) {
                List<String> lines = terrains.route(terrain.tiles().get(row).get(col))
                    .lines(TILE_WIDTH, TILE_HEIGHT);

                for (int line = 0; line < lines.size(); line++) {
                    console.moveCursor(terminalRow(row) + line, terminalCol(col));
                    console.print(lines.get(line));
                }
            }
        }
    }

    private void renderTreasures(List<TreasureDTO> treasures) {
        Map<String, Integer> placedOnTile = new HashMap<>();

        for (TreasureDTO treasure : treasures) {
            String tile = treasure.row() + ":" + treasure.col();
            int index = placedOnTile.merge(tile, 1, Integer::sum) - 1;

            if (index >= ITEM_ROWS * TILE_WIDTH) {
                continue;
            }

            console.moveCursor(terminalRow(treasure.row()) + ITEM_ROW + index / TILE_WIDTH,
                terminalCol(treasure.col()) + index % TILE_WIDTH);
            console.print(String.valueOf(items.symbol(treasure.item())));
        }
    }

    private void renderActors(List<ActorDTO> actors, int playerId) {
        for (ActorDTO actor : actors) {
            Offset offset = placementOf(actor);
            String symbol = this.actors.route(actor).symbol();

            console.moveCursor(terminalRow(actor.row()) + offset.row(),
                terminalCol(actor.col()) + offset.col());
            console.print(actor.id() == playerId ? HIGHLIGHT_ON + symbol + HIGHLIGHT_OFF : symbol);
        }
    }

    private Offset placementOf(ActorDTO actor) {
        Function<ActorDTO, Offset> placement = PLACEMENTS.get(actor.kind());

        if (placement == null) {
            throw new IllegalStateException("No placement registered for " + actor.kind());
        }

        return placement.apply(actor);
    }

    private int terminalRow(int row) {
        return row * (TILE_HEIGHT + 1) + 2;
    }

    private int terminalCol(int col) {
        return col * (TILE_WIDTH + 1) + 2;
    }

    private int borderRow(int row) {
        return row * (TILE_HEIGHT + 1) + 1;
    }

    private int borderCol(int col) {
        return col * (TILE_WIDTH + 1) + 1;
    }

    private record Offset(int row, int col) { }

}
