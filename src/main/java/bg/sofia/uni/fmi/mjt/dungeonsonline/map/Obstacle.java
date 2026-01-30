package bg.sofia.uni.fmi.mjt.dungeonsonline.map;

public class Obstacle implements Tile {

    public Obstacle() {

    }

    @Override
    public String design() {
        return """
                +-----------+
                |X X X X X X|
                | X X X X X |
                |X X X X X X|
                +-----------+""";
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

}
