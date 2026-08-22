package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;

import java.util.HashMap;
import java.util.Map;

public class ItemFormatter {

    private static final char WEAPON_SYMBOL = '⚔';
    private static final char SPELL_SYMBOL = '⚗';
    private static final char HEALTH_POTION_SYMBOL = '❤';
    private static final char MANA_POTION_SYMBOL = '✦';

    private static final String EMPTY_SLOT = "[EMPTY]";
    private static final String MINION_NAME = "Minion";

    private final Map<ItemKind, Character> symbols;
    private final String emptySlot;
    private final String minionName;

    public ItemFormatter() {
        this(defaultSymbols(), EMPTY_SLOT, MINION_NAME);
    }

    public ItemFormatter(Map<ItemKind, Character> symbols, String emptySlot, String minionName) {
        for (ItemKind kind : ItemKind.values()) {
            if (!symbols.containsKey(kind)) {
                throw new IllegalArgumentException("No symbol for " + kind);
            }
        }

        this.symbols = new HashMap<>(symbols);
        this.emptySlot = emptySlot;
        this.minionName = minionName;
    }

    public String format(ItemDTO item) {
        if (item == null) {
            return emptySlot;
        }

        return switch (item.kind()) {
            case WEAPON -> "[" + symbol(item) + " " + item.name()
                + "\tAttack: " + item.power() + "\tLevel: " + item.level() + "]";
            case SPELL -> "[" + symbol(item) + " " + item.name()
                + "\tAttack: " + item.power() + "\tLevel: " + item.level()
                + "\tMana: " + item.manaCost() + "]";
            case HEALTH_POTION, MANA_POTION -> "[" + symbol(item) + " " + item.name()
                + "\tRegenerate: " + item.power() + "]";
        };
    }

    public char symbol(ItemDTO item) {
        return symbols.get(item.kind());
    }

    public String format(ActorDTO actor) {
        return actor.kind() == ActorKind.MINION ? "[" + minionName + "]" : "[Player " + actor.id() + "]";
    }

    private static Map<ItemKind, Character> defaultSymbols() {
        Map<ItemKind, Character> defaults = new HashMap<>();

        defaults.put(ItemKind.WEAPON, WEAPON_SYMBOL);
        defaults.put(ItemKind.SPELL, SPELL_SYMBOL);
        defaults.put(ItemKind.HEALTH_POTION, HEALTH_POTION_SYMBOL);
        defaults.put(ItemKind.MANA_POTION, MANA_POTION_SYMBOL);

        return defaults;
    }

}
