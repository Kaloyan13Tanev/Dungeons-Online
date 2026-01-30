package bg.sofia.uni.fmi.mjt.dungeonsonline.map;

public abstract class Tile {

    protected final static String IMAGE;

    public abstract boolean isWalkable();

    public abstract void print();

}
