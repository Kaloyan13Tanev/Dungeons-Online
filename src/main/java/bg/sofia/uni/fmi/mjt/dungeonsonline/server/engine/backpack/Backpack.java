package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Backpack {

    private static final int DEFAULT_CAPACITY = 10;
    private static final int NO_EMPTY_SLOT = -1;

    private final Item[] slots;

    public Backpack() {
        this(DEFAULT_CAPACITY);
    }

    public Backpack(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive, got " + capacity);
        }

        this.slots = new Item[capacity];
    }

    public Backpack(Item[] slots) {
        if (slots == null || slots.length < 1) {
            throw new IllegalArgumentException("Slots must not be empty");
        }

        this.slots = slots;
    }

    public int capacity() {
        return slots.length;
    }

    public Optional<Item> at(int slot) {
        requireValidSlot(slot);

        return Optional.ofNullable(slots[slot]);
    }

    public List<Item> slots() {
        return Collections.unmodifiableList(Arrays.asList(slots.clone()));
    }

    public void add(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }

        int slot = findEmptySlot();
        if (slot == NO_EMPTY_SLOT) {
            throw new FullBackpackException("Your backpack is full!");
        }

        slots[slot] = item;
    }

    public Optional<Item> remove(int slot) {
        requireValidSlot(slot);

        Item removed = slots[slot];
        slots[slot] = null;

        return Optional.ofNullable(removed);
    }

    private int findEmptySlot() {
        for (int slot = 0; slot < slots.length; slot++) {
            if (slots[slot] == null) {
                return slot;
            }
        }

        return NO_EMPTY_SLOT;
    }

    private void requireValidSlot(int slot) {
        if (slot < 0 || slot >= slots.length) {
            throw new IllegalArgumentException(
                "Slot must be in [0, " + slots.length + "), got " + slot);
        }
    }
}
