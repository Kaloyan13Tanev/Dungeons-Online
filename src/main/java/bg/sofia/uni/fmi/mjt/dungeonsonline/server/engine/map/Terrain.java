package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

public enum Terrain {

    GROUND(true),
    OBSTACLE(false);

    private final boolean walkable;

    Terrain(boolean walkable) {
        this.walkable = walkable;
    }

    public boolean isWalkable() {
        return walkable;
    }

}
