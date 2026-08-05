package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;

public class Treasure {

    private static final int PICKUP_EXPERIENCE = 20;

    private final Item item;
    private final int xp;

    public Treasure(Item item) {
        this(item, PICKUP_EXPERIENCE);
    }

    protected Treasure(Item item, int xp) {
        this.item = item;
        this.xp = xp;
    }

    public Item getItem() {
        return item;
    }

    public int getXp() {
        return xp;
    }
}
