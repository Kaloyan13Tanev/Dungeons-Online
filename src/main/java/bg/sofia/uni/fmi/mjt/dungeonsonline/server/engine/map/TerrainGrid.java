package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.Position;

public class TerrainGrid {

    private final int rows;
    private final int cols;
    private final Position spawnPoint;
    private final Terrain[][] grid;

    public TerrainGrid(Terrain[][] grid, Position spawnPoint) {
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
        requireInside(position);

        return grid[position.row()][position.col()];
    }

    public boolean isWalkable(Position position) {
        return getTerrain(position).isWalkable();
    }

    private void requireInside(Position position) {
        if (!isInside(position)) {
            throw new IllegalArgumentException("Position " + position + " is outside the map");
        }
    }
}
