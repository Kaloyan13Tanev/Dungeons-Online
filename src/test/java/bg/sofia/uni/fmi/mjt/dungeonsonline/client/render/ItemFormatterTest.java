package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemFormatterTest {

    private static final int PLAYER_ID = 1;
    private static final int MINION_ID = 10;

    private static final int ROW = 3;
    private static final int COL = 4;

    private static final int LEVEL = 2;
    private static final int POWER = 40;
    private static final int MANA_COST = 30;
    private static final int NO_LEVEL = 0;
    private static final int NO_MANA_COST = 0;

    private static final String WEAPON_NAME = "Sword";
    private static final String SPELL_NAME = "Fireball";
    private static final String HEALTH_POTION_NAME = "Bandage";
    private static final String MANA_POTION_NAME = "Elixir";

    private static final ItemDTO WEAPON =
        new ItemDTO(ItemKind.WEAPON, WEAPON_NAME, LEVEL, POWER, NO_MANA_COST);
    private static final ItemDTO SPELL =
        new ItemDTO(ItemKind.SPELL, SPELL_NAME, LEVEL, POWER, MANA_COST);
    private static final ItemDTO HEALTH_POTION =
        new ItemDTO(ItemKind.HEALTH_POTION, HEALTH_POTION_NAME, NO_LEVEL, POWER, NO_MANA_COST);
    private static final ItemDTO MANA_POTION =
        new ItemDTO(ItemKind.MANA_POTION, MANA_POTION_NAME, NO_LEVEL, POWER, NO_MANA_COST);

    private static final ActorDTO PLAYER = new ActorDTO(PLAYER_ID, ActorKind.PLAYER, ROW, COL);
    private static final ActorDTO MINION = new ActorDTO(MINION_ID, ActorKind.MINION, ROW, COL);

    private ItemFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new ItemFormatter();
    }

    @Test
    void testConstructorThrowsWhenAKindHasNoSymbol() {
        Map<ItemKind, Character> incomplete = new HashMap<>();
        incomplete.put(ItemKind.WEAPON, 'W');

        assertThrows(IllegalArgumentException.class, () -> new ItemFormatter(incomplete, "", ""),
            "ItemFormatter should throw when a kind of item has no symbol");
    }

    @Test
    void testFormatShowsTheAttackAndTheLevelOfAWeapon() {
        String formatted = formatter.format(WEAPON);

        assertTrue(formatted.contains(WEAPON_NAME), "ItemFormatter should show the name of a weapon");
        assertTrue(formatted.contains(String.valueOf(POWER)), "ItemFormatter should show the attack of a weapon");
        assertTrue(formatted.contains(String.valueOf(LEVEL)), "ItemFormatter should show the level of a weapon");
    }

    @Test
    void testFormatShowsTheManaCostOfASpell() {
        String formatted = formatter.format(SPELL);

        assertTrue(formatted.contains(SPELL_NAME), "ItemFormatter should show the name of a spell");
        assertTrue(formatted.contains(String.valueOf(MANA_COST)),
            "ItemFormatter should show the mana a spell costs");
    }

    @Test
    void testFormatShowsHowMuchAPotionRegenerates() {
        String health = formatter.format(HEALTH_POTION);
        String mana = formatter.format(MANA_POTION);

        assertTrue(health.contains(HEALTH_POTION_NAME), "ItemFormatter should show the name of a health potion");
        assertTrue(health.contains(String.valueOf(POWER)),
            "ItemFormatter should show how much a health potion regenerates");
        assertTrue(mana.contains(MANA_POTION_NAME), "ItemFormatter should show the name of a mana potion");
        assertTrue(mana.contains(String.valueOf(POWER)),
            "ItemFormatter should show what a mana potion regenerates");
    }

    @Test
    void testFormatShowsAnEmptySlotWhenThereIsNoItem() {
        assertEquals("[EMPTY]", formatter.format((ItemDTO) null),
            "ItemFormatter should show an empty slot when there is no item");
    }

    @Test
    void testFormatShowsThePlayerBehindTheirId() {
        assertTrue(formatter.format(PLAYER).contains(String.valueOf(PLAYER_ID)),
            "ItemFormatter should show a player by their id");
    }

    @Test
    void testFormatShowsAMinionByName() {
        assertEquals("[Minion]", formatter.format(MINION),
            "ItemFormatter should show a minion by name");
    }

}
