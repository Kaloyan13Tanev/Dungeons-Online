package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.Position;

public class TerrainGrid {

    private final int rows;
    private final int cols;
    private final Position spawnPoint;
    private final Terrain[][] grid;

    public TerrainGrid(Terrain[][] grid, Position spawnPoint) {
        requireValidGrid(grid);

        if (spawnPoint == null) {
            throw new InvalidSpawnPointException("Spawn point must not be null");
        }

        this.rows = grid.length;
        this.cols = grid[0].length;
        this.spawnPoint = spawnPoint;
        this.grid = grid;

        if (!isInside(spawnPoint)) {
            throw new InvalidSpawnPointException(
                "Spawn point " + spawnPoint + " is outside the " + rows + "x" + cols + " map");
        }

        if (!isWalkable(spawnPoint)) {
            throw new InvalidSpawnPointException(
                "Spawn point " + spawnPoint + " is on " + getTerrain(spawnPoint));
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Position getSpawnPoint() {
        return spawnPoint;
    }

    public boolean isInside(Position position) {
        return position.row() >= 0 && position.row() < rows
            && position.col() >= 0 && position.col() < cols;
    }

    public Terrain getTerrain(Position position) {
        if (!isInside(position)) {
            throw new IllegalArgumentException("Position " + position + " is outside the map");
        }

        return grid[position.row()][position.col()];
    }

    public boolean isWalkable(Position position) {
        return getTerrain(position).isWalkable();
    }

    private static void requireValidGrid(Terrain[][] grid) {
        if (grid == null || grid.length == 0) {
            throw new IllegalArgumentException("Grid must not be null or empty");
        }

        if (grid[0] == null || grid[0].length == 0) {
            throw new IllegalArgumentException("Grid rows must not be null or empty");
        }

        int cols = grid[0].length;
        for (Terrain[] row : grid) {
            if (row == null || row.length != cols) {
                throw new IllegalArgumentException("Every grid row must hold " + cols + " terrains");
            }

            for (Terrain terrain : row) {
                if (terrain == null) {
                    throw new IllegalArgumentException("Grid must not hold a null terrain");
                }
            }
        }
    }

}
