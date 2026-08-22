package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Spell;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Weapon;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.ManaPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {

    private static final int LEVEL = 2;
    private static final int ATTACK = 20;
    private static final int DAMAGE = 40;
    private static final int MANA_COST = 30;
    private static final int HEALING = 25;
    private static final int MANA = 35;

    private static final int NO_LEVEL = 0;
    private static final int NO_MANA_COST = 0;

    private static final Weapon SWORD = new Weapon("Sword", LEVEL, ATTACK);
    private static final Spell FIREBALL = new Spell("Fireball", LEVEL, DAMAGE, MANA_COST);
    private static final HealthPotion BANDAGE = new HealthPotion("Bandage", HEALING);
    private static final ManaPotion ELIXIR = new ManaPotion("Elixir", MANA);

    @Mock
    private Item unknownItem;

    private ItemMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ItemMapper();
    }

    @Test
    void testToDTOMapsAWeaponToItsAttack() {
        assertEquals(new ItemDTO(ItemKind.WEAPON, SWORD.name(), LEVEL, ATTACK, NO_MANA_COST),
            mapper.toDTO(SWORD), "ItemMapper should map a weapon to its name, level and attack");
    }

    @Test
    void testToDTOMapsASpellToItsDamageAndManaCost() {
        assertEquals(new ItemDTO(ItemKind.SPELL, FIREBALL.name(), LEVEL, DAMAGE, MANA_COST),
            mapper.toDTO(FIREBALL), "ItemMapper should map a spell to its name, level, damage and mana cost");
    }

    @Test
    void testToDTOMapsAHealthPotionToItsHealing() {
        assertEquals(new ItemDTO(ItemKind.HEALTH_POTION, BANDAGE.name(), NO_LEVEL, HEALING, NO_MANA_COST),
            mapper.toDTO(BANDAGE), "ItemMapper should map a health potion to its name and healing");
    }

    @Test
    void testToDTOMapsAManaPotionToTheManaItRestores() {
        assertEquals(new ItemDTO(ItemKind.MANA_POTION, ELIXIR.name(), NO_LEVEL, MANA, NO_MANA_COST),
            mapper.toDTO(ELIXIR), "ItemMapper should map a mana potion to its name and mana");
    }

    @Test
    void testToDTOMapsNoItemToNothing() {
        assertNull(mapper.toDTO(null), "ItemMapper should map an empty slot to nothing");
    }

    @Test
    void testToDTOThrowsForAnItemOfAnUnknownKind() {
        assertThrows(IllegalStateException.class, () -> mapper.toDTO(unknownItem),
            "ItemMapper should throw when the item is neither a weapon, a spell nor a potion");
    }

    @Test
    void testToDTOsKeepsTheItemsInTheOrderTheyCame() {
        List<ItemDTO> mapped = mapper.toDTOs(List.of(SWORD, FIREBALL));

        assertEquals(2, mapped.size(), "ItemMapper should map every item it was given");
        assertEquals(ItemKind.WEAPON, mapped.getFirst().kind(),
            "ItemMapper should keep the items in the order they came in");
        assertEquals(ItemKind.SPELL, mapped.getLast().kind(),
            "ItemMapper should keep the items in the order they came in");
    }

    @Test
    void testToDTOsKeepsTheEmptySlotsBetweenTheItems() {
        List<ItemDTO> mapped = mapper.toDTOs(Arrays.asList(SWORD, null, FIREBALL));

        assertEquals(3, mapped.size(), "ItemMapper should map every slot it was given");
        assertNull(mapped.get(1), "ItemMapper should leave an empty slot empty");
    }

}
