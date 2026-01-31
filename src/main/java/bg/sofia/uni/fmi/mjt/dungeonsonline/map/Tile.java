package bg.sofia.uni.fmi.mjt.dungeonsonline.map;

public abstract class Tile {

    private final TileType tileType;
    private final boolean isWalkable;

    public Tile(TileType tileType, boolean isWalkable) {
        this.tileType = tileType;
        this.isWalkable = isWalkable;
    }

    public TileType getTileType() {
        return tileType;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

}
