package bg.sofia.uni.fmi.mjt.dungeonsonline.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.entity.Minion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.entity.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderTile;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class WalkableTile extends Tile {

    Set<Player> players;
    List<Item> items;
    Optional<Minion> minion;

    public WalkableTile(TileType tileType) {
        super(tileType, true);
        players = new HashSet<>();
        items = new ArrayList<>();
        minion = Optional.of(new Minion());
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public Optional<Minion> getMinion() {
        return minion;
    }
}
