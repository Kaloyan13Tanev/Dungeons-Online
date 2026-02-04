package bg.sofia.uni.fmi.mjt.dungeonsonline.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.temp.TempPlayer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Weapon;

public class GameMap {

    private static final Ground G = new Ground();
    private static final Obstacle O = new Obstacle();
    private final Ground SPAWN = new Ground();


    private final  Tile[][] grid = {
            {SPAWN, G, G, G, G, G, G, G, G, G, G},
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

    public GameMap() {
        SPAWN.addPlayer(TempPlayer.THE_PLAYER);
        SPAWN.addItem(new Weapon());
        SPAWN.addItem(new Spell(50));
    }

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
