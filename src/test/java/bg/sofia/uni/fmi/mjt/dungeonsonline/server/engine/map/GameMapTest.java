package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Weapon;
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

    private static final Item ITEM = new Weapon("Sword", 1, 10);

    private static final Random RANDOM = new Random(42);

    @Mock
    TerrainGrid grid;

    Map<Integer, Actor> actors;
    Map<Integer, Treasure> treasures;
    GameMap map;

    Actor firstActor;
    Actor secondActor;
    Treasure firstTreasure;
    Treasure secondTreasure;

    @BeforeEach
    void setUp() {
        actors = new HashMap<>();
        treasures = new HashMap<>();
        map = new GameMap(grid, actors, treasures);

        firstActor = new Player(FIRST_ACTOR_ID, FIRST_POSITION);
        secondActor = new Player(SECOND_ACTOR_ID, SECOND_POSITION);
        firstTreasure = new Treasure(FIRST_TREASURE_ID, FIRST_POSITION, ITEM);
        secondTreasure = new Treasure(SECOND_TREASURE_ID, SECOND_POSITION, ITEM);
    }

    @Test
    void testAddActorStoresTheActorUnderItsId() {
        map.addActor(firstActor);

        assertEquals(1, actors.size(), "GameMap should hold the actor that was added");
        assertEquals(firstActor, actors.get(FIRST_ACTOR_ID),
            "GameMap should store the actor under its own id");
    }

    @Test
    void testAddActorThrowsIfActorAlreadyOnTheMap() {
        map.addActor(firstActor);

        assertThrows(IllegalStateException.class, () -> map.addActor(firstActor),
            "GameMap should refuse an actor whose id is already on the map");
    }

    @Test
    void testRemoveActorTakesActorOffTheMap() {
        map.addActor(firstActor);
        map.removeActor(FIRST_ACTOR_ID);

        assertTrue(actors.isEmpty(), "GameMap should no longer hold an actor that was removed");
    }

    @Test
    void testRemoveActorReturnsTheActorItRemoved() {
        map.addActor(firstActor);

        assertEquals(Optional.of(firstActor), map.removeActor(FIRST_ACTOR_ID),
            "GameMap should return the actor it removed");
    } //TODO:

    @Test
    void testRemoveActorReturnsEmptyIfActorIsNotOnTheMap() {
        assertTrue(map.removeActor(UNKNOWN_ACTOR_ID).isEmpty(),
            "GameMap should return empty when removing an id that is not on the map");
    } //TODO:

    @Test
    void testGetActorReturnsTheActorWithThatId() {
        map.addActor(firstActor);

        assertEquals(Optional.of(firstActor), map.getActor(FIRST_ACTOR_ID),
            "GameMap should return the actor stored under the given id");
    }

    @Test
    void testGetActorReturnsEmptyIfActorIsNotOnTheMap() {
        assertTrue(map.getActor(UNKNOWN_ACTOR_ID).isEmpty(),
            "GameMap should return empty for an id that is not on the map");
    }

    @Test
    void testGetActorsReturnsEveryActorOnTheMap() {
        map.addActor(firstActor);
        map.addActor(secondActor);

        List<Actor> onMap = map.getActors();

        assertEquals(2, onMap.size(), "GameMap should return every actor that was added");
        assertTrue(onMap.containsAll(List.of(firstActor, secondActor)),
            "GameMap should return each of the actors that were added");
    }

    @Test
    void testActorsAtReturnsOnlyTheActorsOnThatPosition() {
        map.addActor(firstActor);
        map.addActor(secondActor);

        assertEquals(List.of(firstActor), map.actorsAt(FIRST_POSITION),
            "GameMap should return only the actors standing on the given position");
    }

    @Test
    void testActorsAtReturnsEmptyIfNobodyStandsThere() {
        map.addActor(firstActor);

        assertTrue(map.actorsAt(SECOND_POSITION).isEmpty(),
            "GameMap should return no actors for a position nobody stands on");
    }

    @Test
    void testActorsAtFollowsTheActorAfterItMoves() {
        Player player = new Player(FIRST_ACTOR_ID, FIRST_POSITION);
        map.addActor(player);

        player.moveTo(SECOND_POSITION);

        assertTrue(map.actorsAt(FIRST_POSITION).isEmpty(),
            "GameMap should not report an actor on the position it left");
        assertEquals(List.of(player), map.actorsAt(SECOND_POSITION),
            "GameMap should report an actor on the position it moved to");
    } //TODO:

    @Test
    void testActorsByPositionGroupsTheActorsSharingAPosition() {
        Actor sameTile = new Player(SECOND_ACTOR_ID, FIRST_POSITION);
        map.addActor(firstActor);
        map.addActor(sameTile);

        Map<Position, List<Actor>> grouped = map.actorsByPosition();

        assertEquals(1, grouped.size(), "GameMap should group actors sharing a position under one key");
        assertTrue(grouped.get(FIRST_POSITION).containsAll(List.of(firstActor, sameTile)),
            "GameMap should put every actor on a position in the same group");
    }

    @Test
    void testAddTreasureStoresTheTreasure() {
        map.addTreasure(firstTreasure);

        assertEquals(1, treasures.size(), "GameMap should hold the treasure that was added");
        assertEquals(firstTreasure, treasures.get(FIRST_TREASURE_ID),
            "GameMap should store the treasure under its own id");
    }

    @Test
    void testAddTreasureThrowsIfTreasureAlreadyOnTheMap() {
        map.addTreasure(firstTreasure);

        assertThrows(IllegalStateException.class, () -> map.addTreasure(firstTreasure),
            "GameMap should refuse a treasure whose id is already on the map");
    }

    @Test
    void testRemoveTreasureTakesTreasureOffTheMap() {
        map.addTreasure(firstTreasure);
        map.removeTreasure(FIRST_TREASURE_ID);

        assertTrue(treasures.isEmpty(), "GameMap should no longer hold a treasure that was removed");
    }

    @Test
    void testRemoveTreasureReturnsTheTreasureItRemoved() {
        map.addTreasure(firstTreasure);

        assertEquals(Optional.of(firstTreasure), map.removeTreasure(FIRST_TREASURE_ID),
            "GameMap should return the treasure it removed");
    } //TODO:

    @Test
    void testRemoveTreasureReturnsEmptyIfTreasureIsNotOnTheMap() {
        assertTrue(map.removeTreasure(UNKNOWN_TREASURE_ID).isEmpty(),
            "GameMap should return empty when removing a treasure id that is not on the map");
    } //TODO:

    @Test
    void testGetTreasureReturnsTheTreasureWithThatId() {
        map.addTreasure(firstTreasure);

        assertEquals(Optional.of(firstTreasure), map.getTreasure(FIRST_TREASURE_ID),
            "GameMap should return the treasure stored under the given id");
    }

    @Test
    void testGetTreasureReturnsEmptyIfTreasureIsNotOnTheMap() {
        assertTrue(map.getTreasure(UNKNOWN_TREASURE_ID).isEmpty(),
            "GameMap should return empty for a treasure id that is not on the map");
    }

    @Test
    void testGetTreasuresReturnsEveryTreasureOnTheMap() {
        map.addTreasure(firstTreasure);
        map.addTreasure(secondTreasure);

        List<Treasure> onMap = map.getTreasures();

        assertEquals(2, onMap.size(), "GameMap should return every treasure that was added");
        assertTrue(onMap.containsAll(List.of(firstTreasure, secondTreasure)),
            "GameMap should return each of the treasures that were added");
    }

    @Test
    void testTreasuresAtReturnsOnlyTheTreasuresOnThatPosition() {
        map.addTreasure(firstTreasure);
        map.addTreasure(secondTreasure);

        assertEquals(List.of(firstTreasure), map.treasuresAt(FIRST_POSITION),
            "GameMap should return only the treasures lying on the given position");
    }

    @Test
    void testTreasuresAtReturnsEmptyIfNothingLiesThere() {
        map.addTreasure(firstTreasure);

        assertTrue(map.treasuresAt(SECOND_POSITION).isEmpty(),
            "GameMap should return no treasures for a position that holds none");
    }

    @Test
    void testTreasuresByPositionGroupsTheTreasuresSharingAPosition() {
        Treasure sameTile = new Treasure(SECOND_TREASURE_ID, FIRST_POSITION, ITEM);
        map.addTreasure(firstTreasure);
        map.addTreasure(sameTile);

        Map<Position, List<Treasure>> grouped = map.treasuresByPosition();

        assertEquals(1, grouped.size(),
            "GameMap should group treasures sharing a position under one key");
        assertTrue(grouped.get(FIRST_POSITION).containsAll(List.of(firstTreasure, sameTile)),
            "GameMap should put every treasure on a position in the same group");
    }

    @Test
    void testRandomFreePositionReturnsAWalkablePosition() {
        stubGridSize();
        when(grid.isWalkable(any(Position.class))).thenReturn(false);
        when(grid.isWalkable(SECOND_POSITION)).thenReturn(true);

        assertEquals(Optional.of(SECOND_POSITION), map.randomFreePosition(RANDOM),
            "GameMap should return the only walkable position on the map");
    }

    @Test
    void testRandomFreePositionSkipsPositionsAnActorStandsOn() {
        stubGridSize();
        when(grid.isWalkable(any(Position.class))).thenReturn(false);
        when(grid.isWalkable(FIRST_POSITION)).thenReturn(true);
        when(grid.isWalkable(SECOND_POSITION)).thenReturn(true);
        map.addActor(firstActor);

        assertEquals(Optional.of(SECOND_POSITION), map.randomFreePosition(RANDOM),
            "GameMap should never return a position an actor stands on");
    }

    @Test
    void testRandomFreePositionReturnsEmptyIfNothingIsFree() {
        stubGridSize();
        when(grid.isWalkable(any(Position.class))).thenReturn(false);
        when(grid.isWalkable(FIRST_POSITION)).thenReturn(true);
        map.addActor(firstActor);

        assertTrue(map.randomFreePosition(RANDOM).isEmpty(),
            "GameMap should return empty when every walkable position is taken");
    }

    private void stubGridSize() {
        when(grid.getRows()).thenReturn(ROWS);
        when(grid.getCols()).thenReturn(COLS);
    }

}
