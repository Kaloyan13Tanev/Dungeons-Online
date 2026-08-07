package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;

public class Treasure {

    private static final int PICKUP_EXPERIENCE = 20;

    private final int id;
    private final Position position;
    private final Item item;
    private final int xp;

    public Treasure(int id, Position position, Item item) {
        this(id, position, item, PICKUP_EXPERIENCE);
    }

    protected Treasure(int id, Position position, Item item, int xp) {
        this.id = id;
        this.position = position;
        this.item = item;
        this.xp = xp;
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public Item getItem() {
        return item;
    }

    public int getXp() {
        return xp;
    }
}
