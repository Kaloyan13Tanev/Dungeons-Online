package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;

public class DroppedTreasure extends Treasure {

    public DroppedTreasure(int id, Position position, Item item) {
        super(id, position, item, 0);
    }
}
