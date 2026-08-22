package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.Terrain;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.TerrainGrid;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.TerrainKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TerrainMapperTest {

    private static final TerrainKind G = TerrainKind.GROUND;
    private static final TerrainKind O = TerrainKind.OBSTACLE;

    private static final int ROWS = 2;
    private static final int COLS = 3;

    private static final Position OBSTACLE_POSITION = new Position(1, 2);

    private static final TerrainDTO EXPECTED = new TerrainDTO(List.of(
        List.of(G, G, G),
        List.of(G, G, O)));

    @Mock
    private TerrainGrid grid;

    private TerrainMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TerrainMapper();
    }

    @Test
    void testToDTOMapsEveryTileOfTheGrid() {
        when(grid.getRows()).thenReturn(ROWS);
        when(grid.getCols()).thenReturn(COLS);
        when(grid.getTerrain(any(Position.class))).thenReturn(Terrain.GROUND);
        when(grid.getTerrain(OBSTACLE_POSITION)).thenReturn(Terrain.OBSTACLE);

        assertEquals(EXPECTED, mapper.toDTO(grid),
            "TerrainMapper should map every tile of the grid to its own kind");
    }

}
