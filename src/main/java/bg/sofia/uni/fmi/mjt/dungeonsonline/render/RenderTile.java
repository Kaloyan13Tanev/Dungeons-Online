package bg.sofia.uni.fmi.mjt.dungeonsonline.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.entity.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.map.Tile;
import bg.sofia.uni.fmi.mjt.dungeonsonline.map.WalkableTile;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class RenderTile {
    
    private static final int TILE_WIDTH = 11;
    private static final int TILE_HEIGHT = 4;
    private static final int PLAYER_ROW = 0;
    private static final int MINION_ROW = 1;
    private static final char MINION_SYMBOL = 'M';
    private static final int ITEM_ROW = 2;
    private static final int ITEM_ROWS_COUNT = 2;

    public static void render(WalkableTile tile, int row, int col) {
        switch (tile.getTileType()) {
            case GROUND -> renderGround(row, col, tile);
        }

        terminal.writer().flush();
    }

    public static void render(Tile tile, int row, int col) {
        switch (tile.getTileType()) {
            case OBSTACLE -> renderObstacle(row, col);
        }

        terminal.writer().flush();
    }

    public static void render(WalkableTile tile, Player player) {
        int row = getPlayerTerminalRow(player);
        int col = getPlayerTerminalCol(player);

        render(tile, row, col);
    }

    private static void renderTile(int row, int col, TileRenderer renderer) {
        for (int i = 0; i < TILE_HEIGHT; i++) {
            Console.moveCursor(row + i, col);

            renderer.render(i);
        }
    }

    private static void renderObstacle(int row, int col) {
        renderTile(row, col, (i) -> {
            for (int j = 0; j < TILE_WIDTH; j++) {
                if (i % 2 == j % 2) {
                    terminal.writer().print("X");
                } else {
                    terminal.writer().print(" ");
                }
            }
        });
    }

    private static void renderGround(int row, int col, WalkableTile tile) {
        renderTile(row, col, (i) -> {
            terminal.writer().print(" ".repeat(TILE_WIDTH));
        });
        renderPlayers(row, col, tile);
        renderMinion(row, col, tile);
        renderItems(row, col, tile);
    }

    private static void renderPlayers(int row, int col, WalkableTile tile) {
        for (Player player : tile.getPlayers()) {
            Console.moveCursor(PLAYER_ROW + row, col + player.getPlayerID() - 1);
            terminal.writer().print(player.getPlayerID());
        }
    }

    private static int getPlayerTerminalRow(Player player) {
        return (player.getX() * (TILE_HEIGHT + 1)) + 2;
    }

    private static int getPlayerTerminalCol(Player player) {
        return (player.getY() * (TILE_WIDTH + 1)) + 2;
    }

    private static void renderMinion(int row, int col, WalkableTile tile) {
        if (tile.getMinion().isPresent()) {
            Console.moveCursor(row + MINION_ROW, col + (TILE_WIDTH / 2));
            terminal.writer().print(MINION_SYMBOL);
        }
    }

    private static void renderItems(int row, int col, WalkableTile tile) {
        RenderItem renderItem = new RenderItem();

        int max = ITEM_ROWS_COUNT * TILE_WIDTH;

        if (!tile.getItems().isEmpty()) {
            for (int i = 0; i < tile.getItems().size() && i < max; i++) {
                int currRow = row + ITEM_ROW + (i / TILE_WIDTH);
                int currCol = col + (i % TILE_WIDTH);

                Console.moveCursor(currRow, currCol);

                terminal.writer().print(renderItem.renderOnTile(tile.getItems().get(i)));
            }
        }
    }
}
