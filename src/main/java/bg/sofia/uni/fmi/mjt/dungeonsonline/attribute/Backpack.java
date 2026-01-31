package bg.sofia.uni.fmi.mjt.dungeonsonline.attribute;

import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Item;

public class Backpack {

    private static final int SIZE = 10;

    private final Item[] backpack = new Item[SIZE];

    public Item get(int index) {
        return backpack[index];
    }
}
