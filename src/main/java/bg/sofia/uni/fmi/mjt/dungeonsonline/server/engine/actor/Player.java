package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.Backpack;

public class Player extends AbstractActor {

    private static final int SLOT_COUNT = 10;
    private static final int FIRST_SLOT = 0;

    private final Backpack backpack;
    private final Level level;

    private int selectedSlot;

    public Player(int id, Position position) {
        super(id, new Stats(), position);

        this.backpack = new Backpack();
        this.level = new Level();
        this.selectedSlot = FIRST_SLOT;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public Level getLevel() {
        return level;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void select(int slot) {
        if (slot < FIRST_SLOT || slot >= SLOT_COUNT) {
            throw new IllegalArgumentException(
                "slot must be in [" + FIRST_SLOT + ", " + SLOT_COUNT + "), got " + slot);
        }

        selectedSlot = slot;
    }

    public void gainExperience(int amount) {
        int gained = level.gain(amount);

        if (gained > 0) {
            getStats().levelUp(gained);
        }
    }
}
