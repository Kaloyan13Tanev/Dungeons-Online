package bg.sofia.uni.fmi.mjt.dungeonsonline.treasure;

import java.util.Objects;

public class Spell extends Item {

    private final int manaCost;

    public Spell(int manaCost) {
        this.manaCost = manaCost;
    }

    @Override
    public void use() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Spell spell = (Spell) o;
        return manaCost == spell.manaCost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), manaCost);
    }

    public int getManaCost() {
        return manaCost;
    }
}
