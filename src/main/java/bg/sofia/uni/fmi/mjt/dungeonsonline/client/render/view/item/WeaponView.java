package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.item;

public record WeaponView(String name, int level, int attack) implements ItemView {

    private static final char SYMBOL = '⚔';

    @Override
    public char symbol() {
        return SYMBOL;
    }

    @Override
    public String toString() {
        return SYMBOL + " " + name + "\tAttack: " + attack + "\tLevel: " + level;
    }

}
