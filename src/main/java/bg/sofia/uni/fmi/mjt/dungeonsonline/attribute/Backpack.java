package bg.sofia.uni.fmi.mjt.dungeonsonline.attribute;

import bg.sofia.uni.fmi.mjt.dungeonsonline.exception.BackpackIsFullException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderBackpack;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderItem;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Item;

public class Backpack {

    private static final int SIZE = 10;
    private static final RenderBackpack RENDER_BACKPACK = new RenderBackpack();

    private final Item[] backpack = new Item[SIZE];

    public int getSize() {
        return backpack.length;
    }

    public Item get(int index) {
        if (index < 0 || index >= SIZE) {
            throw new IndexOutOfBoundsException("Backpack items can be accessed with 0 to 9 keys!");
        }

        return backpack[index];
    }

    public int findEmptySlot() {
        for (int i = 0; i < backpack.length; i++) {
            if (backpack[i] == null) {
                return i;
            }
        }
        return -1;  // Inventory full
    }

    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null!");
        }

        int idx = findEmptySlot();

        if (idx == -1) {
            throw new BackpackIsFullException("Cannot add item! Backpack is full!");
        }

        backpack[idx] = item;

        RENDER_BACKPACK.renderBackpack(this);
    }

}
