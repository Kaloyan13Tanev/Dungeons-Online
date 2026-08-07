package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Stats;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.ItemLevelTooHighException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Spell;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Weapon;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.ManaPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.InvalidMoveException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.TerrainGrid;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.SequentialIdGenerator;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameEngineImplTest {

    private static final Position SPAWN_POINT = new Position(0, 1);
    private static final Position FREE_POSITION = new Position(0, 0);
    private static final Position OBSTACLE_POSITION = new Position(1, 1);
    private static final Position OUTSIDE_POSITION = new Position(-1, 1);

    private static final int FIRST_PLAYER_ID = 1;
    private static final int SECOND_PLAYER_ID = 2;
    private static final int UNKNOWN_PLAYER_ID = 9;

    private static final int FIRST_MINION_ID = 10;
    private static final int FIRST_TREASURE_ID = 0;

    private static final int FIRST_SLOT = 0;
    private static final int LAST_SLOT = 9;

    private static final int BARE_HANDED_DAMAGE = 25;
    private static final int ARMED_DAMAGE = 45;
    private static final int NO_DAMAGE = 0;
    private static final int TARGET_DEFENSE = 50;
    private static final int UNBEATABLE_DEFENSE = 200;

    private static final int SEED = 42;

    private static final Weapon SWORD = new Weapon("Sword", 1, 20);
    private static final Weapon EXCALIBUR = new Weapon("Excalibur", 5, 100);
    private static final Spell FIREBALL = new Spell("Fireball", 1, 40, 30);
    private static final Spell METEOR = new Spell("Meteor", 5, 200, 10);
    private static final HealthPotion BANDAGE = new HealthPotion("Bandage", 30);
    private static final ManaPotion ELIXIR = new ManaPotion("Elixir", 30);

    @Mock
    private GameMap map;
    @Mock
    private TerrainGrid terrain;
    @Mock
    private Actor target;

    private Stats targetStats;

    private SequentialIdGenerator treasureIds;
    private SequentialIdGenerator minionIds;
    private HashMap<Integer, Player> players;
    private GameEngineImpl engine;

    @BeforeEach
    void setUp() {
        treasureIds = new SequentialIdGenerator(FIRST_TREASURE_ID);
        minionIds = new SequentialIdGenerator(FIRST_MINION_ID);
        players = new HashMap<>();
        engine = new GameEngineImpl(map, treasureIds, minionIds, new Random(SEED), players);
    }

    @Test
    void testConstructorThrowsIfAnyParameterIsNull() {
        Random random = new Random(SEED);

        assertThrows(IllegalArgumentException.class,
            () -> new GameEngineImpl(null, treasureIds, minionIds, random),
            "GameEngineImpl should throw when the map is null");
        assertThrows(IllegalArgumentException.class,
            () -> new GameEngineImpl(map, null, minionIds, random),
            "GameEngineImpl should throw when the treasure id generator is null");
        assertThrows(IllegalArgumentException.class,
            () -> new GameEngineImpl(map, treasureIds, null, random),
            "GameEngineImpl should throw when the minion id generator is null");
        assertThrows(IllegalArgumentException.class,
            () -> new GameEngineImpl(map, treasureIds, minionIds, null),
            "GameEngineImpl should throw when the random is null");
    }

    @Test
    void testJoinAddsPlayer() {
        when(map.getTerrainGrid()).thenReturn(terrain);
        when(terrain.getSpawnPoint()).thenReturn(SPAWN_POINT);

        engine.join(FIRST_PLAYER_ID);

        assertEquals(1, players.size(), "GameEngineImpl should add a player when join is called");
        assertTrue(players.containsKey(FIRST_PLAYER_ID), "GameEngineImpl should add the player with the correct id");
    }

    @Test
    void testJoinThrowsIfPlayerAlreadyExists() {
        when(map.getTerrainGrid()).thenReturn(terrain);
        when(terrain.getSpawnPoint()).thenReturn(SPAWN_POINT);

        engine.join(FIRST_PLAYER_ID);

        assertThrows(IllegalArgumentException.class, () -> engine.join(FIRST_PLAYER_ID),
            "GameEngineImpl should throw when a player with the same id already exists");
    }

    @Test
    void testLeaveRemovesPlayer() {
        players.put(FIRST_PLAYER_ID, new Player(FIRST_PLAYER_ID, SPAWN_POINT));
        players.put(SECOND_PLAYER_ID, new Player(SECOND_PLAYER_ID, SPAWN_POINT));

        engine.leave(FIRST_PLAYER_ID);

        assertEquals(1, players.size(), "GameEngineImpl should remove a player when leave is called");
        assertFalse(players.containsKey(FIRST_PLAYER_ID),
            "GameEngineImpl should no longer hold the player that left");
        assertTrue(players.containsKey(SECOND_PLAYER_ID),
            "GameEngineImpl should keep the players that did not leave");
        verify(map).removeActor(FIRST_PLAYER_ID);
    }

    @Test
    void testLeaveIgnoresUnknownId() {
        players.put(FIRST_PLAYER_ID, new Player(FIRST_PLAYER_ID, SPAWN_POINT));

        engine.leave(UNKNOWN_PLAYER_ID);

        assertEquals(1, players.size(),
            "GameEngineImpl should not change when an unknown player leaves");
        verify(map, never()).removeActor(anyInt());
    }

    @Test
    void testMoveChangesPlayerPosition() {
        Player player = new Player(FIRST_PLAYER_ID, SPAWN_POINT);
        players.put(FIRST_PLAYER_ID, player);

        when(map.getTerrainGrid()).thenReturn(terrain);
        when(terrain.isInside(FREE_POSITION)).thenReturn(true);
        when(terrain.isWalkable(FREE_POSITION)).thenReturn(true);

        engine.move(FIRST_PLAYER_ID, Direction.LEFT);

        assertEquals(FREE_POSITION, player.getPosition(),
            "GameEngineImpl should move the player one tile in the given direction");
    }

    @Test
    void testMoveThrowsIfTargetIsOutsideTheMap() {
        Player player = new Player(FIRST_PLAYER_ID, SPAWN_POINT);
        players.put(FIRST_PLAYER_ID, player);

        when(map.getTerrainGrid()).thenReturn(terrain);
        when(terrain.isInside(OUTSIDE_POSITION)).thenReturn(false);

        assertThrows(InvalidMoveException.class, () -> engine.move(FIRST_PLAYER_ID, Direction.UP),
            "GameEngineImpl should throw when the player tries to leave the map");
        assertEquals(SPAWN_POINT, player.getPosition(),
            "GameEngineImpl should leave the player where they were when the move is refused");
    }

    @Test
    void testMoveThrowsIfTargetIsNotWalkable() {
        Player player = new Player(FIRST_PLAYER_ID, SPAWN_POINT);
        players.put(FIRST_PLAYER_ID, player);

        when(map.getTerrainGrid()).thenReturn(terrain);
        when(terrain.isInside(OBSTACLE_POSITION)).thenReturn(true);
        when(terrain.isWalkable(OBSTACLE_POSITION)).thenReturn(false);

        assertThrows(InvalidMoveException.class, () -> engine.move(FIRST_PLAYER_ID, Direction.DOWN),
            "GameEngineImpl should throw when the player walks into an obstacle");
        assertEquals(SPAWN_POINT, player.getPosition(),
            "GameEngineImpl should leave the player where they were when the move is refused");
    }

    @Test
    void testMoveThrowsIfPlayerIsNotInTheGame() {
        assertThrows(IllegalStateException.class, () -> engine.move(UNKNOWN_PLAYER_ID, Direction.LEFT),
            "GameEngineImpl should throw when an unknown player tries to move");
    }

    @Test
    void testSelectChangesTheSelectedSlot() {
        Player player = new Player(FIRST_PLAYER_ID, SPAWN_POINT);
        players.put(FIRST_PLAYER_ID, player);

        engine.select(FIRST_PLAYER_ID, LAST_SLOT);

        assertEquals(LAST_SLOT, player.getSelectedSlot(),
            "GameEngineImpl should select the slot the player asked for");
    }

    @Test
    void testSelectThrowsIfSlotIsOutOfRange() {
        Player player = new Player(FIRST_PLAYER_ID, SPAWN_POINT);
        players.put(FIRST_PLAYER_ID, player);

        assertThrows(IllegalArgumentException.class, () -> engine.select(FIRST_PLAYER_ID, -1),
            "GameEngineImpl should throw when the slot is negative");
        assertThrows(IllegalArgumentException.class,
            () -> engine.select(FIRST_PLAYER_ID, player.getBackpack().capacity()),
            "GameEngineImpl should throw when the slot is past the last one");
        assertEquals(FIRST_SLOT, player.getSelectedSlot(),
            "GameEngineImpl should keep the previous slot when the new one is refused");
    }

    @Test
    void testSelectThrowsIfPlayerIsNotInTheGame() {
        assertThrows(IllegalStateException.class, () -> engine.select(UNKNOWN_PLAYER_ID, FIRST_SLOT),
            "GameEngineImpl should throw when an unknown player selects a slot");
    }

    @Test
    void testUseAttacksBareHandedWhenNothingIsSelected() {
        players.put(FIRST_PLAYER_ID, new Player(FIRST_PLAYER_ID, SPAWN_POINT));
        standOnTheSameTile();
        when(target.getStats()).thenReturn(targetStats);
        when(targetStats.getDefense()).thenReturn(TARGET_DEFENSE);

        engine.use(FIRST_PLAYER_ID, SECOND_PLAYER_ID);

        verify(targetStats).takeDamage(BARE_HANDED_DAMAGE);
    } //TODO: check testing later

    @Test
    void testUseAddsTheAttackOfTheSelectedWeapon() {
        Player player = new Player(FIRST_PLAYER_ID, SPAWN_POINT);
        player.getBackpack().add(SWORD);
        players.put(FIRST_PLAYER_ID, player);

        standOnTheSameTile();
        when(target.getStats()).thenReturn(targetStats);
        when(targetStats.getDefense()).thenReturn(TARGET_DEFENSE);

        engine.use(FIRST_PLAYER_ID, SECOND_PLAYER_ID);

        verify(targetStats).takeDamage(ARMED_DAMAGE);
    }

    @Test
    void testUseNeverDealsNegativeDamage() {
        players.put(FIRST_PLAYER_ID, new Player(FIRST_PLAYER_ID, SPAWN_POINT));
        standOnTheSameTile();
        when(target.getStats()).thenReturn(targetStats);
        when(targetStats.getDefense()).thenReturn(UNBEATABLE_DEFENSE);

        engine.use(FIRST_PLAYER_ID, SECOND_PLAYER_ID);

        verify(targetStats).takeDamage(NO_DAMAGE);
    }

    @Test
    void testUseThrowsWhenTheWeaponIsAboveThePlayerLevel() {
        Player player = new Player(FIRST_PLAYER_ID, SPAWN_POINT);
        player.getBackpack().add(EXCALIBUR);
        players.put(FIRST_PLAYER_ID, player);

        standOnTheSameTile();

        assertThrows(ItemLevelTooHighException.class, () -> engine.use(FIRST_PLAYER_ID, SECOND_PLAYER_ID),
            "GameEngineImpl should throw when the weapon needs a higher level than the player has");
        verify(targetStats, never()).takeDamage(anyInt());
    }

    @Test
    void testUseThrowsWhenThereIsNoTarget() {
        players.put(FIRST_PLAYER_ID, new Player(FIRST_PLAYER_ID, SPAWN_POINT));

        assertThrows(TargetNotReachableException.class, () -> engine.use(FIRST_PLAYER_ID, null),
            "GameEngineImpl should throw when an attack comes without a target");
    }

    @Test
    void testUseThrowsWhenTheTargetIsOnAnotherTile() {
        players.put(FIRST_PLAYER_ID, new Player(FIRST_PLAYER_ID, SPAWN_POINT));
        when(map.getActor(SECOND_PLAYER_ID)).thenReturn(Optional.of(target));
        when(target.isAlive()).thenReturn(true);
        when(target.getPosition()).thenReturn(FREE_POSITION);

        assertThrows(TargetNotReachableException.class, () -> engine.use(FIRST_PLAYER_ID, SECOND_PLAYER_ID),
            "GameEngineImpl should throw when the target stands on another tile");
        verify(targetStats, never()).takeDamage(anyInt());
    }

    @Test
    void testUseThrowsWhenTheTargetIsNotOnTheMap() {
        players.put(FIRST_PLAYER_ID, new Player(FIRST_PLAYER_ID, SPAWN_POINT));
        when(map.getActor(UNKNOWN_PLAYER_ID)).thenReturn(Optional.empty());

        assertThrows(TargetNotReachableException.class, () -> engine.use(FIRST_PLAYER_ID, UNKNOWN_PLAYER_ID),
            "GameEngineImpl should throw when the target is no longer on the map");
    }

    @Test
    void testUseThrowsWhenThePlayerTargetsThemselves() {
        players.put(FIRST_PLAYER_ID, new Player(FIRST_PLAYER_ID, SPAWN_POINT));

        assertThrows(IllegalArgumentException.class, () -> engine.use(FIRST_PLAYER_ID, FIRST_PLAYER_ID),
            "GameEngineImpl should throw when a player attacks themselves");
    }

    private void standOnTheSameTile() {
        when(map.getActor(SECOND_PLAYER_ID)).thenReturn(Optional.of(target));
        when(target.isAlive()).thenReturn(true);
        when(target.getPosition()).thenReturn(SPAWN_POINT);
    }

}
