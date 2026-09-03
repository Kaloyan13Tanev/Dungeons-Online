package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.item;

public record ManaPotionView(String name, int mana) implements ItemView {

    private static final char SYMBOL = '✦';

    @Override
    public char symbol() {
        return SYMBOL;
    }

    @Override
    public String toString() {
        return SYMBOL + " " + name + "\tRegenerate: " + mana;
    }

}
