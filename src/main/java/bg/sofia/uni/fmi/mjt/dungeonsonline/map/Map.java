package bg.sofia.uni.fmi.mjt.dungeonsonline.map;

public class Map {

    public static final Ground G = new Ground();
    public static final Obstacle O = new Obstacle();
    public static final  Tile[][] MAP = {
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

    //players //items //minion?
}
