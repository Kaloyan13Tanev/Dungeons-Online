package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TerrainGridTest {

    private static final Terrain G = Terrain.GROUND;
    private static final Terrain O = Terrain.OBSTACLE;

    private static final Terrain[][] GRID = {
            {G, O, G, G},
            {G, G, G, G},
            {G, G, G, G}
    };

    private static final Position SPAWN_POINT = new Position(0, 0);
    private static final Position OBSTACLE_POSITION = new Position(0, 1);

    private TerrainGrid grid;

    @BeforeEach
    void setUp() {
        grid = new TerrainGrid(GRID, SPAWN_POINT);
    }

    @Test
    void testConstructorThrowsIfGridIsNullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new TerrainGrid(null, SPAWN_POINT),
            "TerrainGrid should throw when the grid is null");
        assertThrows(IllegalArgumentException.class, () -> new TerrainGrid(new Terrain[0][0], SPAWN_POINT),
            "TerrainGrid should throw when the grid has no rows");
        assertThrows(IllegalArgumentException.class, () -> new TerrainGrid(new Terrain[][] {{}}, SPAWN_POINT),
            "TerrainGrid should throw when the grid has no columns");
        assertThrows(IllegalArgumentException.class, () -> new TerrainGrid(new Terrain[][] {null}, SPAWN_POINT),
            "TerrainGrid should throw when a grid row is null");
    }

    @Test
    void testConstructorThrowsIfRowsHaveDifferentLengths() {
        Terrain[][] jagged = {
            {G, G, G},
            {G, G}
        };

        assertThrows(IllegalArgumentException.class, () -> new TerrainGrid(jagged, SPAWN_POINT),
            "TerrainGrid should throw when the rows do not have the same length");
    }

    @Test
    void testConstructorThrowsIfGridHoldsANullTerrain() {
        Terrain[][] withNull = {
            {G, null},
            {G, G}
        };

        assertThrows(IllegalArgumentException.class, () -> new TerrainGrid(withNull, SPAWN_POINT),
            "TerrainGrid should throw when a cell of the grid is null");
    }

    @Test
    void testConstructorThrowsIfSpawnPointIsNull() {
        assertThrows(InvalidSpawnPointException.class, () -> new TerrainGrid(GRID, null),
            "TerrainGrid should throw when the spawn point is null");
    }

    @Test
    void testConstructorThrowsIfSpawnPointIsOutOfBounds() {
        assertThrows(InvalidSpawnPointException.class,
            () -> new TerrainGrid(GRID, new Position(GRID.length, 0)),
            "TerrainGrid should throw when the spawn point is outside the grid");
    }

    @Test
    void testConstructorThrowsIfSpawnPointIsNotWalkable() {
        assertThrows(InvalidSpawnPointException.class, () -> new TerrainGrid(GRID, OBSTACLE_POSITION),
                "TerrainGrid should throw when the spawn point is not walkable");
    }

    @Test
    void testIsInsideAcceptsEveryCornerOfTheGrid() {
        assertTrue(grid.isInside(new Position(0, 0)),
            "TerrainGrid should treat the first row and column as inside");
        assertTrue(grid.isInside(new Position(0, GRID[0].length - 1)),
            "TerrainGrid should treat the last column as inside");
        assertTrue(grid.isInside(new Position(GRID.length - 1, 0)),
            "TerrainGrid should treat the last row as inside");
        assertTrue(grid.isInside(new Position(GRID.length - 1, GRID[0].length - 1)),
            "TerrainGrid should treat the last row and column as inside");
    } //TODO:

    @Test
    void testIsInsideReturnsFalseIfPositionIsOutOfBounds() {
        assertFalse(grid.isInside(new Position(GRID.length, 0)),
            "TerrainGrid should treat a row past the last one as outside");
        assertFalse(grid.isInside(new Position(0, GRID[0].length)),
            "TerrainGrid should treat a column past the last one as outside");
        assertFalse(grid.isInside(new Position(-1, 0)),
                "TerrainGrid should treat a negative row as outside");
        assertFalse(grid.isInside(new Position(0, -1)),
                "TerrainGrid should treat a negative column as outside");
    }

    @Test
    void testGetTerrainReturnsRightType() {
        assertEquals(Terrain.GROUND, grid.getTerrain(SPAWN_POINT));
        assertEquals(Terrain.OBSTACLE, grid.getTerrain(OBSTACLE_POSITION));
    }

    @Test
    void testGetTerrainThrowsIfPositionIsOutOfBounds() {
        assertThrows(IllegalArgumentException.class,
            () -> grid.getTerrain(new Position(GRID.length, 0)),
            "TerrainGrid should throw when asked for the terrain outside the grid");
    }

}
