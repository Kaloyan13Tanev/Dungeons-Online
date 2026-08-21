package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.Treasure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameMapTest {

    private static final int ROWS = 2;
    private static final int COLS = 2;

    private static final Position FIRST_POSITION = new Position(0, 0);
    private static final Position SECOND_POSITION = new Position(1, 1);

    private static final int FIRST_ACTOR_ID = 1;
    private static final int SECOND_ACTOR_ID = 2;
    private static final int UNKNOWN_ACTOR_ID = 9;

    private static final int FIRST_TREASURE_ID = 1;
    private static final int SECOND_TREASURE_ID = 2;
    private static final int UNKNOWN_TREASURE_ID = 9;

    private static final int FIRST_FREE_INDEX = 0;

    @Mock
    TerrainGrid grid;
    @Mock
    Actor firstActor;
    @Mock
    Actor secondActor;
    @Mock
    Treasure firstTreasure;
    @Mock
    Treasure secondTreasure;
    @Mock
    Random random;

    Map<Integer, Actor> actors;
    Map<Integer, Treasure> treasures;
    GameMap map;

    @BeforeEach
    void setUp() {
        actors = new HashMap<>();
        treasures = new HashMap<>();
        map = new GameMap(grid, actors, treasures);
    }

    @Test
    void testAddActorStoresTheActorUnderItsId() {
        add(firstActor, FIRST_ACTOR_ID);

        assertEquals(1, actors.size(), "GameMap should hold the actor that was added");
        assertEquals(firstActor, actors.get(FIRST_ACTOR_ID),
            "GameMap should store the actor under its own id");
    }

    @Test
    void testAddActorThrowsIfActorAlreadyOnTheMap() {
        add(firstActor, FIRST_ACTOR_ID);

        assertThrows(IllegalStateException.class, () -> map.addActor(firstActor),
            "GameMap should refuse an actor whose id is already on the map");
    }

    @Test
    void testRemoveActorTakesActorOffTheMap() {
        add(firstActor, FIRST_ACTOR_ID);
        map.removeActor(FIRST_ACTOR_ID);

        assertTrue(actors.isEmpty(), "GameMap should no longer hold an actor that was removed");
    }

    @Test
    void testRemoveActorReturnsTheActorItRemoved() {
        add(firstActor, FIRST_ACTOR_ID);

        assertEquals(Optional.of(firstActor), map.removeActor(FIRST_ACTOR_ID),
            "GameMap should return the actor it removed");
    }

    @Test
    void testRemoveActorReturnsEmptyIfActorIsNotOnTheMap() {
        assertTrue(map.removeActor(UNKNOWN_ACTOR_ID).isEmpty(),
            "GameMap should return empty when removing an id that is not on the map");
    }

    @Test
    void testActorsAtReturnsOnlyTheActorsOnThatPosition() {
        standAt(firstActor, FIRST_ACTOR_ID, FIRST_POSITION);
        standAt(secondActor, SECOND_ACTOR_ID, SECOND_POSITION);

        assertEquals(List.of(firstActor), map.actorsAt(FIRST_POSITION),
            "GameMap should return only the actors standing on the given position");
    }

    @Test
    void testActorsAtReturnsEmptyIfNobodyStandsThere() {
        standAt(firstActor, FIRST_ACTOR_ID, FIRST_POSITION);

        assertTrue(map.actorsAt(SECOND_POSITION).isEmpty(),
            "GameMap should return no actors for a position nobody stands on");
    }

    @Test
    void testActorsAtFollowsTheActorAfterItMoves() {
        add(firstActor, FIRST_ACTOR_ID);
        when(firstActor.getPosition()).thenReturn(FIRST_POSITION, SECOND_POSITION);

        assertEquals(List.of(firstActor), map.actorsAt(FIRST_POSITION),
            "GameMap should report an actor on the position it stands on");
        assertEquals(List.of(firstActor), map.actorsAt(SECOND_POSITION),
            "GameMap should report an actor on the position it moved to");
    }

    @Test
    void testActorsByPositionGroupsTheActorsSharingAPosition() {
        standAt(firstActor, FIRST_ACTOR_ID, FIRST_POSITION);
        standAt(secondActor, SECOND_ACTOR_ID, FIRST_POSITION);

        Map<Position, List<Actor>> grouped = map.actorsByPosition();

        assertEquals(1, grouped.size(), "GameMap should group actors sharing a position under one key");
        assertTrue(grouped.get(FIRST_POSITION).containsAll(List.of(firstActor, secondActor)),
            "GameMap should put every actor on a position in the same group");
    }

    @Test
    void testAddTreasureStoresTheTreasure() {
        add(firstTreasure, FIRST_TREASURE_ID);

        assertEquals(1, treasures.size(), "GameMap should hold the treasure that was added");
        assertEquals(firstTreasure, treasures.get(FIRST_TREASURE_ID),
            "GameMap should store the treasure under its own id");
    }

    @Test
    void testAddTreasureThrowsIfTreasureAlreadyOnTheMap() {
        add(firstTreasure, FIRST_TREASURE_ID);

        assertThrows(IllegalStateException.class, () -> map.addTreasure(firstTreasure),
            "GameMap should refuse a treasure whose id is already on the map");
    }

    @Test
    void testRemoveTreasureTakesTreasureOffTheMap() {
        add(firstTreasure, FIRST_TREASURE_ID);
        map.removeTreasure(FIRST_TREASURE_ID);

        assertTrue(treasures.isEmpty(), "GameMap should no longer hold a treasure that was removed");
    }

    @Test
    void testRemoveTreasureReturnsTheTreasureItRemoved() {
        add(firstTreasure, FIRST_TREASURE_ID);

        assertEquals(Optional.of(firstTreasure), map.removeTreasure(FIRST_TREASURE_ID),
            "GameMap should return the treasure it removed");
    }

    @Test
    void testRemoveTreasureReturnsEmptyIfTreasureIsNotOnTheMap() {
        assertTrue(map.removeTreasure(UNKNOWN_TREASURE_ID).isEmpty(),
            "GameMap should return empty when removing a treasure id that is not on the map");
    }

    @Test
    void testTreasuresAtReturnsOnlyTheTreasuresOnThatPosition() {
        lieAt(firstTreasure, FIRST_TREASURE_ID, FIRST_POSITION);
        lieAt(secondTreasure, SECOND_TREASURE_ID, SECOND_POSITION);

        assertEquals(List.of(firstTreasure), map.treasuresAt(FIRST_POSITION),
            "GameMap should return only the treasures lying on the given position");
    }

    @Test
    void testTreasuresAtReturnsEmptyIfNothingLiesThere() {
        lieAt(firstTreasure, FIRST_TREASURE_ID, FIRST_POSITION);

        assertTrue(map.treasuresAt(SECOND_POSITION).isEmpty(),
            "GameMap should return no treasures for a position that holds none");
    } //TODO: should i test non throwing behaviour

    @Test
    void testTreasuresByPositionGroupsTheTreasuresSharingAPosition() {
        lieAt(firstTreasure, FIRST_TREASURE_ID, FIRST_POSITION);
        lieAt(secondTreasure, SECOND_TREASURE_ID, FIRST_POSITION);

        Map<Position, List<Treasure>> grouped = map.treasuresByPosition();

        assertEquals(1, grouped.size(),
            "GameMap should group treasures sharing a position under one key");
        assertTrue(grouped.get(FIRST_POSITION).containsAll(List.of(firstTreasure, secondTreasure)),
            "GameMap should put every treasure on a position in the same group");
    }

    @Test
    void testRandomFreePositionReturnsAWalkablePosition() {
        mockGridSize();
        when(grid.isWalkable(any(Position.class))).thenReturn(false);
        when(grid.isWalkable(SECOND_POSITION)).thenReturn(true);
        when(random.nextInt(anyInt())).thenReturn(FIRST_FREE_INDEX);

        assertEquals(Optional.of(SECOND_POSITION), map.randomFreePosition(random),
            "GameMap should return the only walkable position on the map");
    }

    @Test
    void testRandomFreePositionSkipsPositionsAnActorStandsOn() {
        mockGridSize();
        when(grid.isWalkable(any(Position.class))).thenReturn(false);
        when(grid.isWalkable(FIRST_POSITION)).thenReturn(true);
        when(grid.isWalkable(SECOND_POSITION)).thenReturn(true);
        when(random.nextInt(anyInt())).thenReturn(FIRST_FREE_INDEX);
        standAt(firstActor, FIRST_ACTOR_ID, FIRST_POSITION);

        assertEquals(Optional.of(SECOND_POSITION), map.randomFreePosition(random),
            "GameMap should never return a position an actor stands on");
    }

    @Test
    void testRandomFreePositionReturnsEmptyIfNothingIsFree() {
        mockGridSize();
        when(grid.isWalkable(any(Position.class))).thenReturn(false);
        when(grid.isWalkable(FIRST_POSITION)).thenReturn(true);
        standAt(firstActor, FIRST_ACTOR_ID, FIRST_POSITION);

        assertTrue(map.randomFreePosition(random).isEmpty(),
            "GameMap should return empty when every walkable position is taken");
    }

    private void add(Actor actor, int id) {
        when(actor.getId()).thenReturn(id);

        map.addActor(actor);
    }

    private void standAt(Actor actor, int id, Position position) {
        when(actor.getPosition()).thenReturn(position);

        add(actor, id);
    }

    private void add(Treasure treasure, int id) {
        when(treasure.getId()).thenReturn(id);

        map.addTreasure(treasure);
    }

    private void lieAt(Treasure treasure, int id, Position position) {
        when(treasure.getPosition()).thenReturn(position);

        add(treasure, id);
    }

    private void mockGridSize() {
        when(grid.getRows()).thenReturn(ROWS);
        when(grid.getCols()).thenReturn(COLS);
    }

}
