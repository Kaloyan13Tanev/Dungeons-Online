package bg.sofia.uni.fmi.mjt.dungeonsonline.attribute;

import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Item;

public class Backpack {

    private static final int SIZE = 10;

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
}
