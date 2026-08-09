package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.Mapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.Terrain;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.TerrainGrid;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.TerrainKind;

import java.util.ArrayList;
import java.util.List;

public class TerrainMapper implements Mapper<TerrainGrid, TerrainDTO> {

    @Override
    public TerrainDTO toDTO(TerrainGrid terrain) {
        List<List<TerrainKind>> tiles = new ArrayList<>(terrain.getRows());

        for (int row = 0; row < terrain.getRows(); row++) {
            List<TerrainKind> line = new ArrayList<>(terrain.getCols());

            for (int col = 0; col < terrain.getCols(); col++) {
                line.add(kind(terrain.getTerrain(new Position(row, col))));
            }

            tiles.add(List.copyOf(line));
        }

        return new TerrainDTO(List.copyOf(tiles));
    }

    private TerrainKind kind(Terrain terrain) {
        return switch (terrain) {
            case GROUND -> TerrainKind.GROUND;
            case OBSTACLE -> TerrainKind.OBSTACLE;
        };
    }

}
