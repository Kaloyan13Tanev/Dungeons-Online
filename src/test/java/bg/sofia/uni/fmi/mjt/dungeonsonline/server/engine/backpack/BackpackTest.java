package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.ManaPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BackpackTest {

    private static final int SMALL_CAPACITY = 3;

    private static final int FIRST_SLOT = 0;
    private static final int SECOND_SLOT = 1;

    private static final Item WEAPON = new Weapon("Sword", 1, 10);
    private static final Item POTION = new ManaPotion("Mana potion", 20);

    private Backpack backpack;

    @BeforeEach
    void setUp() {
        backpack = new Backpack(SMALL_CAPACITY);
    }

    @Test
    void testConstructorThrowsWhenCapacityIsNotPositive() {
        assertThrows(IllegalArgumentException.class, () -> new Backpack(0),
            "Backpack should throw when the capacity is zero");
        assertThrows(IllegalArgumentException.class, () -> new Backpack(-1),
            "Backpack should throw when the capacity is negative");
    }

    @Test
    void testAddReturnsTrueWhenThereIsRoom() {
        assertTrue(backpack.add(WEAPON), "Backpack should return true while it has an empty slot");
    }

    @Test
    void testAddReturnsFalseAndKeepsTheItemsWhenFull() {
        fill(backpack);

        assertFalse(backpack.add(POTION), "Backpack should return false when every slot is taken");
        assertEquals(Optional.of(WEAPON), backpack.at(FIRST_SLOT),
            "Backpack should not replace an item when it has no room");
    }

    @Test
    void testRemoveTakesTheItemOutOfItsSlot() {
        Item[] slots = {WEAPON, POTION, WEAPON};
        Backpack filled = new Backpack(slots);

        filled.remove(SECOND_SLOT);

        assertNull(slots[SECOND_SLOT], "Backpack should leave nothing in the slot it removed from");
    }

    @Test
    void testAddThrowsOnNullItem() {
        assertThrows(IllegalArgumentException.class, () -> backpack.add(null),
            "Backpack should throw when asked to hold nothing");
    }

    @Test
    void testAtReturnsEmptyForAnEmptySlot() {
        assertTrue(backpack.at(FIRST_SLOT).isEmpty(),
            "Backpack should return empty for a slot that holds no item");
    }

    @Test
    void testAtThrowsForASlotOutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> backpack.at(SMALL_CAPACITY),
            "Backpack should throw when asked for a slot past the last one");
        assertThrows(IllegalArgumentException.class, () -> backpack.at(-1),
            "Backpack should throw when asked for a negative slot");
    }

    @Test
    void testRemoveTakesTheItemOutAndReturnsIt() {
        backpack.add(WEAPON);

        assertEquals(Optional.of(WEAPON), backpack.remove(FIRST_SLOT),
            "Backpack should return the item it removed");
        assertTrue(backpack.at(FIRST_SLOT).isEmpty(),
            "Backpack should leave the slot empty after removing its item");
    }

    @Test
    void testRemoveReturnsEmptyForAnEmptySlot() {
        assertTrue(backpack.remove(FIRST_SLOT).isEmpty(),
            "Backpack should return empty when removing from a slot that holds no item");
    }

    @Test
    void testRemoveThrowsForASlotOutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> backpack.remove(SMALL_CAPACITY),
            "Backpack should throw when removing a slot past the last one");
        assertThrows(IllegalArgumentException.class, () -> backpack.remove(-1),
            "Backpack should throw when removing a negative slot");
    }

    @Test
    void testRemoveLeavesTheOtherSlotsWhereTheyAre() {
        fill(backpack);

        backpack.remove(FIRST_SLOT);

        assertEquals(Optional.of(WEAPON), backpack.at(SECOND_SLOT),
            "Backpack should not shift the items that follow an emptied slot");
    } //TODO:

    @Test
    void testSlotsReturnsEverySlotIncludingTheEmptyOnes() {
        backpack.add(WEAPON);

        List<Item> slots = backpack.slots();

        assertEquals(SMALL_CAPACITY, slots.size(), "Backpack should return one entry per slot");
        assertEquals(WEAPON, slots.get(FIRST_SLOT), "Backpack should return the item a slot holds");
        assertEquals(Collections.nCopies(SMALL_CAPACITY - 1, null), slots.subList(1, SMALL_CAPACITY),
            "Backpack should return null for the slots that hold nothing");
    }

    private void fill(Backpack target) {
        for (int slot = 0; slot < target.capacity(); slot++) {
            target.add(WEAPON);
        }
    }

}
