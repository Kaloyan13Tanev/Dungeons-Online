package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Spell;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Weapon;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.ManaPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.Mapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;

public class ItemMapper implements Mapper<Item, ItemDTO> {

    private static final int NO_LEVEL = 0;
    private static final int NO_MANA_COST = 0;

    @Override
    public ItemDTO toDTO(Item item) {
        return switch (item) {
            case null -> null;
            case Weapon weapon ->
                new ItemDTO(ItemKind.WEAPON, weapon.name(), weapon.level(), weapon.attack(), NO_MANA_COST);
            case Spell spell ->
                new ItemDTO(ItemKind.SPELL, spell.name(), spell.level(), spell.damage(), spell.manaCost());
            case HealthPotion potion ->
                new ItemDTO(ItemKind.HEALTH_POTION, potion.name(), NO_LEVEL, potion.healing(), NO_MANA_COST);
            case ManaPotion potion ->
                new ItemDTO(ItemKind.MANA_POTION, potion.name(), NO_LEVEL, potion.mana(), NO_MANA_COST);
            default -> throw new IllegalStateException("Unknown item " + item.name());
        };
    }

}
