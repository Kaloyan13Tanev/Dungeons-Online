package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.item;

public record SpellView(String name, int level, int damage, int manaCost) implements ItemView {

    private static final char SYMBOL = '⚗';

    @Override
    public char symbol() {
        return SYMBOL;
    }

    @Override
    public String toString() {
        return SYMBOL + " " + name + "\tAttack: " + damage + "\tLevel: " + level + "\tMana: " + manaCost;
    }

}
