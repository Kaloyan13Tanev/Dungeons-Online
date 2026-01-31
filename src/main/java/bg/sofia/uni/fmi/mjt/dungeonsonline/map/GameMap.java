package bg.sofia.uni.fmi.mjt.dungeonsonline.map;

public class GameMap {

    private static final Ground G = new Ground();
    private static final Obstacle O = new Obstacle();

    private final  Tile[][] grid = {
            {G, G, G, G, G, G, G, G, G, G, G},
            {G, G, G, O, O, G, G, G, G, G, G},
            {G, G, G, O, O, O, G, G, G, G, G},
            {G, G, G, G, O, O, G, G, G, G, G},
            {G, G, G, G, G, G, G, G, G, G, G},
            {G, G, G, G, G, G, O, O, G, G, G},
            {G, G, G, G, G, O, O, O, G, G, G},
            {G, G, G, G, G, G, O, O, G, G, G},
            {G, G, G, G, G, G, G, G, G, G, G},
            {G, O, O, G, G, G, G, G, O, O, G},
            {G, G, G, G, G, G, G, G, G, G, G}
    };

    public int getWidth() {
        return grid.length;
    }

    public int getHeight() {
        return grid[0].length;
    }

    public Tile getTile(int row, int col) {
        return grid[row][col];
    }
}
