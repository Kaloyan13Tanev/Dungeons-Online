package bg.sofia.uni.fmi.mjt.dungeonsonline.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.entity.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.map.Tile;
import bg.sofia.uni.fmi.mjt.dungeonsonline.map.WalkableTile;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class RenderTile {
    
    private static final int TILE_WIDTH = 11;
    private static final int TILE_HEIGHT = 4;
    private static final int PLAYER_ROW = 0;

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
        renderPlayers(tile);
        //renderItems();
    }

    private static void renderPlayers(WalkableTile tile) {
        for (Player player : tile.getPlayers()) {
            Console.moveCursor(PLAYER_ROW + getPlayerTerminalRow(player),
                    getPlayerTerminalCol(player) + player.getPlayerID() - 1);
            terminal.writer().print(player.getPlayerID());
        }
    }

    private static int getPlayerTerminalRow(Player player) {
        return (player.getX() * (TILE_HEIGHT + 1)) + 2;
    }

    private static int getPlayerTerminalCol(Player player) {
        return (player.getY() * (TILE_WIDTH + 1)) + 2;
    }
}
