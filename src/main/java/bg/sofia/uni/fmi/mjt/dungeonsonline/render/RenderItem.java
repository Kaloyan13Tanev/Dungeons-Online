package bg.sofia.uni.fmi.mjt.dungeonsonline.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.ManaPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Weapon;

public class RenderItem {

    private static final char WEAPON_SYMBOL = '⚔';
    private static final char SPELL_SYMBOL = '⚗';
    private static final char HP_SYMBOL = '❤';
    private static final char MP_SYMBOL = '✦';

    public String render(Item item) {
        return switch (item) {
            case null -> renderEmptySlot();
            case HealthPotion hpPotion -> renderHealthPotion(hpPotion);
            case ManaPotion manaPotion -> renderManaPotion(manaPotion);
            case Spell spell -> renderSpell(spell);
            case Weapon weapon -> renderWeapon(weapon);
            default -> throw new IllegalStateException("Unexpected value: " + item);
        };
    }

    public char renderOnTile(Item item) {
        return switch (item) {
            case HealthPotion hpPotion -> HP_SYMBOL;
            case ManaPotion manaPotion -> MP_SYMBOL;
            case Spell spell -> SPELL_SYMBOL;
            case Weapon weapon -> WEAPON_SYMBOL;
            default -> throw new IllegalStateException("Unexpected value: " + item);
        };
    }

    private String renderEmptySlot() {
        return "[EMPTY]";
    }

    private String renderWeapon(Weapon weapon) {
        return "[" + WEAPON_SYMBOL + "\tAttack: " + weapon.getAttack() + "\tLevel: " + weapon.getLevel() + "]";
    }

    private String renderSpell(Spell spell) {
        return "[" + SPELL_SYMBOL + "\tAttack: " + spell.getAttack() + "\tLevel: " + spell.getLevel() +
                "\tMana: " + spell.getManaCost() + "]";
    }
    
    private String renderHealthPotion(HealthPotion healthPotion) {
        return "[" + HP_SYMBOL + "\tRegenerate: " + healthPotion.getHealthCharge() + "\tLevel: "
                + healthPotion.getLevel() + "\tMana: " + healthPotion.getManaCost() + "]";
    }
    
    private String renderManaPotion(ManaPotion manaPotion) {
        return "[" + MP_SYMBOL + "\tRegenerate: " + manaPotion.getManaCharge()
                + "\tLevel: " + manaPotion.getLevel() + "]";
    }

}
