package bg.sofia.uni.fmi.mjt.dungeonsonline.treasure;

import java.util.Objects;

public abstract class Item implements Treasure {

    protected final int level;
    protected final int attack;

    public Item() {
        this.level = 0;
        this.attack = 0;
    }

    public Item(int level, int attack) {
        this.level = level;
        this.attack = attack;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return level == item.level && attack == item.attack;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, attack);
    }

    public int getLevel() {
        return level;
    }

    public int getAttack() {
        return attack;
    }
}
