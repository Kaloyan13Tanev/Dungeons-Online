package bg.sofia.uni.fmi.mjt.dungeonsonline.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.entity.Minion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.entity.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Item;

import java.util.List;
import java.util.Optional;

public class Ground extends Tile {

    List<Player> players;
    List<Item> items;
    Optional<Minion> minion;

    public Ground() {
        super(TileType.GROUND, true);
    }
}
