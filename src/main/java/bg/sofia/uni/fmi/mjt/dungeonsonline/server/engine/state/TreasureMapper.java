package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.Mapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;

public class TreasureMapper implements Mapper<Treasure, TreasureDTO> {

    private final ItemMapper items;

    public TreasureMapper(ItemMapper items) {
        this.items = items;
    }

    @Override
    public TreasureDTO toDTO(Treasure treasure) {
        return new TreasureDTO(
            treasure.getId(),
            items.toDTO(treasure.getItem()),
            treasure.getPosition().row(),
            treasure.getPosition().col()
        );
    }

}
