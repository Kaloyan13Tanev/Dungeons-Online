package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;

public class Backpack {

    private static final int DEFAULT_ITEM_COUNT = 10;

    private final int itemCount;
    private final Item[] items;

    public Backpack() {
        this.itemCount = DEFAULT_ITEM_COUNT;
        this.items = new Item[DEFAULT_ITEM_COUNT];
    }

    public Backpack(int itemCount) {
        this.itemCount = itemCount;
        this.items = new Item[itemCount];
    }

    public Backpack(Item[] items) {
        this.items = items;
        this.itemCount = items.length;
    }

    public void addItem(Item item) {
        int index = findEmptySlot();
        if (index == -1) {
            throw new FullBackpackException("Backpack is full, cannot add item");
        }
        items[index] = item;
    }

    public Item removeItem(int index) {
        if (index < 0 || index >= itemCount) {
            throw new IllegalArgumentException("Invalid item index");
        }
        Item removedItem = items[index];
        items[index] = null;
        return removedItem;
    }

    private int findEmptySlot() {
        for (int i = 0; i < itemCount; i++) {
            if (items[i] == null) return i;
        }

        return -1;
    }

}
