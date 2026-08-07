package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.Backpack;

public class Player extends AbstractActor {

    private static final int FIRST_SLOT = 0;

    private final Backpack backpack;
    private final Level level;

    private int selectedSlot;

    public Player(int id, Position position) {
        super(id, new PlayerStats(), position);

        this.backpack = new Backpack();
        this.level = new Level();
        this.selectedSlot = FIRST_SLOT;
    }

    @Override
    public PlayerStats getStats() {
        return (PlayerStats) super.getStats();
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
        if (slot < FIRST_SLOT || slot >= backpack.capacity()) {
            throw new IllegalArgumentException(
                "Slot must be in [" + FIRST_SLOT + ", " + backpack.capacity() + "), got " + slot);
        }

        selectedSlot = slot;
    }

    public void gainExperience(int amount) {
        int gained = level.addXp(amount);

        if (gained > 0) {
            getStats().levelUp(gained);
        }
    }
}
