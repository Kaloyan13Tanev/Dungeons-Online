package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Level;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Minion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.PlayerStats;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Stats;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.Backpack;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.EmptySlotException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.ItemLevelTooHighException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Spell;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Weapon;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.InvalidMoveException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.TerrainGrid;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state.GameStateProducer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.DroppedTreasure;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.IdGenerator;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.TerrainKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    private static final int SECOND_MINION_ID = 11;
    private static final int MINION_XP = 50;

    private static final int TREASURE_ID = 1;
    private static final int UNKNOWN_TREASURE_ID = 9;
    private static final int TREASURE_XP = 20;

    private static final int SECOND_LEVEL = 2;

    private static final int FIRST_SLOT = 0;
    private static final int SECOND_SLOT = 1;

    private static final TerrainDTO TERRAIN = new TerrainDTO(List.of(List.of(TerrainKind.GROUND)));

    private static final int FIRST_LEVEL = 1;

    private static final Spell FIREBALL = new Spell("Fireball", 1, 40, 30);
    private static final Spell METEOR = new Spell("Meteor", 5, 200, 10);

    private static final int TARGET_DEFENSE = 50;
    private static final int SPELL_HIT = 15;

    private static final int POTION_HEALING = 30;
    private static final HealthPotion BANDAGE = new HealthPotion("Bandage", POTION_HEALING);

    private static final Weapon SWORD = new Weapon("Sword", 1, 20);
    private static final Weapon EXCALIBUR = new Weapon("Excalibur", 5, 100);

    private static final int PLAYER_ATTACK = 50;
    private static final int ARMED_HIT = 45;
    private static final int BARE_HANDED_HIT = 25;

    private static final int UNBEATABLE_DEFENSE = 200;
    private static final int NO_DAMAGE = 0;

    @Mock
    private GameMap map;
    @Mock
    private TerrainGrid terrain;
    @Mock
    private IdGenerator<Integer> treasureIds;
    @Mock
    private IdGenerator<Integer> minionIds;
    @Mock
    private Random random;
    @Mock
    private GameStateProducer stateProducer;

    @Mock
    private Player player;
    @Mock
    private Player otherPlayer;
    @Mock
    private PlayerStats playerStats;
    @Mock
    private Level level;
    @Mock
    private Backpack backpack;

    @Mock
    private PlayerStats otherPlayerStats;
    @Mock
    private Backpack otherBackpack;

    @Mock
    private Item unknownItem;

    @Mock
    private Actor target;
    @Mock
    private Minion minion;
    @Mock
    private Stats targetStats;
    @Mock
    private Treasure treasure;

    private Map<Integer, Player> players;
    private GameEngineImpl engine;

    @BeforeEach
    void setUp() {
        players = new HashMap<>();
        engine = new GameEngineImpl(map, treasureIds, minionIds, random, players, stateProducer);
    }

    //TODO: should i tests whether the things are called correctly

    @Test
    void testJoinAddsThePlayerAtTheSpawnPoint() {
        mockSpawnPoint();

        engine.join(FIRST_PLAYER_ID);

        assertTrue(players.containsKey(FIRST_PLAYER_ID),
            "GameEngineImpl should hold the player that joined");
        assertEquals(SPAWN_POINT, players.get(FIRST_PLAYER_ID).getPosition(),
            "GameEngineImpl should start the player that joined on the spawn point");
    }

    @Test
    void testJoinPutsThePlayerOnTheMap() {
        mockSpawnPoint();

        engine.join(FIRST_PLAYER_ID);

        verify(map).addActor(players.get(FIRST_PLAYER_ID));
    }

    @Test
    void testJoinThrowsWhenThePlayerIsAlreadyInTheGame() {
        players.put(FIRST_PLAYER_ID, player);

        assertThrows(IllegalStateException.class, () -> engine.join(FIRST_PLAYER_ID),
            "GameEngineImpl should throw when a player that is already in the game joins");
    }

    @Test
    void testJoinTellsEveryoneElseThePlayerJoined() {
        mockSpawnPoint();
        players.put(SECOND_PLAYER_ID, player);

        List<GameEvent> events = engine.join(FIRST_PLAYER_ID);

        assertEquals(1, events.size(), "GameEngineImpl should report one event when a player joins");
        assertEquals(Set.of(SECOND_PLAYER_ID), events.getFirst().recipients(),
            "GameEngineImpl should tell everyone but the player who joined");
    }

    @Test
    void testLeaveRemovesThePlayerFromTheGame() {
        players.put(FIRST_PLAYER_ID, player);
        players.put(SECOND_PLAYER_ID, otherPlayer);

        engine.leave(FIRST_PLAYER_ID);

        assertEquals(1, players.size(), "GameEngineImpl should no longer hold the player that left");
        assertTrue(players.containsKey(SECOND_PLAYER_ID),
            "GameEngineImpl should keep the players that did not leave");
    }

    @Test
    void testLeaveTakesThePlayerOffTheMap() {
        players.put(FIRST_PLAYER_ID, player);

        engine.leave(FIRST_PLAYER_ID);

        verify(map).removeActor(FIRST_PLAYER_ID);
    }

    @Test
    void testLeaveIgnoresAPlayerThatIsNotInTheGame() {
        players.put(FIRST_PLAYER_ID, player);

        List<GameEvent> events = engine.leave(UNKNOWN_PLAYER_ID);

        assertEquals(1, players.size(),
            "GameEngineImpl should not change when a player that is not in the game leaves");
        assertTrue(events.isEmpty(),
            "GameEngineImpl should report no events when a player that is not in the game leaves");
        verify(map, never()).removeActor(anyInt());
    }

    @Test
    void testLeaveTellsOthersThatThePlayerLeft() {
        players.put(FIRST_PLAYER_ID, player);
        players.put(SECOND_PLAYER_ID, otherPlayer);

        List<GameEvent> events = engine.leave(FIRST_PLAYER_ID);

        assertEquals(1, events.size(), "GameEngineImpl should report one event when a player leaves");
        assertEquals(Set.of(SECOND_PLAYER_ID), events.getFirst().recipients(),
            "GameEngineImpl should tell the players that are left that a player left");
    }

    

    @Test
    void testMoveMovesThePlayerOneTileInTheGivenDirection() {
        players.put(FIRST_PLAYER_ID, player);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(map.getTerrainGrid()).thenReturn(terrain);
        when(terrain.isInside(FREE_POSITION)).thenReturn(true);
        when(terrain.isWalkable(FREE_POSITION)).thenReturn(true);

        engine.move(FIRST_PLAYER_ID, Direction.LEFT);

        verify(player).moveTo(FREE_POSITION);
    }

    @Test
    void testMoveThrowsWhenTheTargetIsOutsideTheMap() {
        players.put(FIRST_PLAYER_ID, player);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(map.getTerrainGrid()).thenReturn(terrain);
        when(terrain.isInside(OUTSIDE_POSITION)).thenReturn(false);

        assertThrows(InvalidMoveException.class, () -> engine.move(FIRST_PLAYER_ID, Direction.UP),
            "GameEngineImpl should throw when a player tries to leave the map");
    }

    @Test
    void testMoveThrowsWhenTheTargetIsNotWalkable() {
        players.put(FIRST_PLAYER_ID, player);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(map.getTerrainGrid()).thenReturn(terrain);
        when(terrain.isInside(OBSTACLE_POSITION)).thenReturn(true);
        when(terrain.isWalkable(OBSTACLE_POSITION)).thenReturn(false);

        assertThrows(InvalidMoveException.class, () -> engine.move(FIRST_PLAYER_ID, Direction.DOWN),
            "GameEngineImpl should throw when a player walks into something in their way");
    }

    @Test
    void testMoveThrowsWhenThePlayerIsNotInTheGame() {
        assertThrows(IllegalStateException.class, () -> engine.move(UNKNOWN_PLAYER_ID, Direction.LEFT),
            "GameEngineImpl should throw when a player that is not in the game moves");
    }

    @Test
    void testSelectSelectsTheSlotOnThePlayer() {
        players.put(FIRST_PLAYER_ID, player);

        engine.select(FIRST_PLAYER_ID, SECOND_SLOT);

        verify(player).select(SECOND_SLOT);
    }

    @Test
    void testSelectThrowsWhenThePlayerIsNotInTheGame() {
        assertThrows(IllegalStateException.class, () -> engine.select(UNKNOWN_PLAYER_ID, FIRST_SLOT),
            "GameEngineImpl should throw when a player that is not in the game selects a slot");
    }

    @Test
    void testUseCastsTheSelectedSpellAtTheOthersOnTheTile() {
        players.put(FIRST_PLAYER_ID, player);
        mockSelected(FIREBALL);
        mockPlayerLevel(FIRST_LEVEL);
        when(player.getStats()).thenReturn(playerStats);
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(map.actorsAt(SPAWN_POINT)).thenReturn(List.of(player, target));
        when(target.getId()).thenReturn(FIRST_MINION_ID);
        when(target.isAlive()).thenReturn(true);
        when(target.getStats()).thenReturn(targetStats);
        when(targetStats.getDefense()).thenReturn(TARGET_DEFENSE);

        engine.use(FIRST_PLAYER_ID, null);

        verify(targetStats).takeDamage(SPELL_HIT);
        verify(playerStats, never()).takeDamage(anyInt());
    }

    @Test
    void testUseSpendsTheManaOfTheSelectedSpell() {
        players.put(FIRST_PLAYER_ID, player);
        mockSelected(FIREBALL);
        mockPlayerLevel(FIRST_LEVEL);
        when(player.getStats()).thenReturn(playerStats);

        engine.use(FIRST_PLAYER_ID, null);

        verify(playerStats).spendMana(FIREBALL.manaCost());
    }

    @Test
    void testUseThrowsWhenTheSpellIsAboveThePlayerLevel() {
        players.put(FIRST_PLAYER_ID, player);
        mockSelected(METEOR);
        mockPlayerLevel(FIRST_LEVEL);

        assertThrows(ItemLevelTooHighException.class, () -> engine.use(FIRST_PLAYER_ID, null),
            "GameEngineImpl should throw when the spell needs a higher level than the player has");
        verify(playerStats, never()).spendMana(anyInt());
    }

    @Test
    void testUseReportsEveryHitOfTheSpellToEveryone() {
        players.put(FIRST_PLAYER_ID, player);
        players.put(SECOND_PLAYER_ID, otherPlayer);
        mockSelected(FIREBALL);
        mockPlayerLevel(FIRST_LEVEL);
        mockTargetDefense(TARGET_DEFENSE);
        when(player.getStats()).thenReturn(playerStats);
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(map.actorsAt(SPAWN_POINT)).thenReturn(List.of(target));
        when(target.getId()).thenReturn(FIRST_MINION_ID);
        when(target.isAlive()).thenReturn(true);

        List<GameEvent> events = engine.use(FIRST_PLAYER_ID, null);

        assertEquals(1, events.size(), "GameEngineImpl should report one event per actor the spell hit");
        assertEquals(Set.of(FIRST_PLAYER_ID, SECOND_PLAYER_ID), events.getFirst().recipients(),
                "GameEngineImpl should tell every player about a hit");
    }

    @Test
    void testUseDrinksTheSelectedPotion() {
        players.put(FIRST_PLAYER_ID, player);
        mockSelected(BANDAGE);
        when(player.getStats()).thenReturn(playerStats);

        engine.use(FIRST_PLAYER_ID, null);

        verify(playerStats).heal(POTION_HEALING);
    }

    @Test
    void testUseTakesTheDrunkPotionOutOfTheBackpack() {
        players.put(FIRST_PLAYER_ID, player);
        mockSelected(BANDAGE);
        when(player.getStats()).thenReturn(playerStats);

        engine.use(FIRST_PLAYER_ID, null);

        verify(backpack).remove(FIRST_SLOT);
    }

    @Test
    void testUseReportsNoEventsWhenDrinkingAPotion() {
        players.put(FIRST_PLAYER_ID, player);
        mockSelected(BANDAGE);
        when(player.getStats()).thenReturn(playerStats);

        List<GameEvent> events = engine.use(FIRST_PLAYER_ID, null);

        assertTrue(events.isEmpty(), "GameEngineImpl should report no events when a player drinks a potion");
    }

    @Test
    void testUseAddsTheAttackOfTheSelectedWeapon() {
        players.put(FIRST_PLAYER_ID, player);
        mockSelected(SWORD);
        mockPlayerLevel(FIRST_LEVEL);
        mockTargetOnTheSameTile();
        mockTargetDefense(TARGET_DEFENSE);
        when(player.getStats()).thenReturn(playerStats);
        when(playerStats.getAttack()).thenReturn(PLAYER_ATTACK);

        engine.use(FIRST_PLAYER_ID, FIRST_MINION_ID);

        verify(targetStats).takeDamage(ARMED_HIT);
    }

    @Test
    void testUseAttacksBareHandedWhenNothingIsSelected() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();
        mockTargetOnTheSameTile();
        mockTargetDefense(TARGET_DEFENSE);
        when(player.getStats()).thenReturn(playerStats);
        when(playerStats.getAttack()).thenReturn(PLAYER_ATTACK);

        engine.use(FIRST_PLAYER_ID, FIRST_MINION_ID);

        verify(targetStats).takeDamage(BARE_HANDED_HIT);
    }

    @Test
    void testUseReportsTheHitOfTheAttackToEveryone() {
        players.put(FIRST_PLAYER_ID, player);
        players.put(SECOND_PLAYER_ID, otherPlayer);
        mockNothingSelected();
        mockTargetOnTheSameTile();
        mockTargetDefense(TARGET_DEFENSE);
        when(player.getStats()).thenReturn(playerStats);
        when(playerStats.getAttack()).thenReturn(PLAYER_ATTACK);

        List<GameEvent> events = engine.use(FIRST_PLAYER_ID, FIRST_MINION_ID);

        assertEquals(1, events.size(), "GameEngineImpl should report one event when a player attacks");
        assertEquals(Set.of(FIRST_PLAYER_ID, SECOND_PLAYER_ID), events.getFirst().recipients(),
            "GameEngineImpl should tell every player about a hit");
    }

    @Test
    void testUseNeverDealsNegativeDamage() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();
        mockTargetOnTheSameTile();
        mockTargetDefense(UNBEATABLE_DEFENSE);
        when(player.getStats()).thenReturn(playerStats);
        when(playerStats.getAttack()).thenReturn(PLAYER_ATTACK);

        engine.use(FIRST_PLAYER_ID, FIRST_MINION_ID);

        verify(targetStats).takeDamage(NO_DAMAGE);
    }

    @Test
    void testUseThrowsWhenTheWeaponIsAboveThePlayerLevel() {
        players.put(FIRST_PLAYER_ID, player);
        mockSelected(EXCALIBUR);
        mockPlayerLevel(FIRST_LEVEL);
        mockTargetOnTheSameTile();
        when(player.getStats()).thenReturn(playerStats);

        assertThrows(ItemLevelTooHighException.class,
            () -> engine.use(FIRST_PLAYER_ID, FIRST_MINION_ID),
            "GameEngineImpl should throw when the weapon needs a higher level than the player has");
        verify(targetStats, never()).takeDamage(anyInt());
    }

    @Test
    void testUseThrowsWhenThereIsNothingToAttack() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();
        when(player.getPosition()).thenReturn(SPAWN_POINT);

        assertThrows(TargetNotReachableException.class, () -> engine.use(FIRST_PLAYER_ID, null),
            "GameEngineImpl should throw when an attack comes without a target");
    }

    @Test
    void testUseThrowsWhenTheTargetStandsOnAnotherTile() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(map.getActor(FIRST_MINION_ID)).thenReturn(Optional.of(target));
        when(target.isAlive()).thenReturn(true);
        when(target.getPosition()).thenReturn(FREE_POSITION);

        assertThrows(TargetNotReachableException.class,
            () -> engine.use(FIRST_PLAYER_ID, FIRST_MINION_ID),
            "GameEngineImpl should throw when the target stands on another tile");
        verify(targetStats, never()).takeDamage(anyInt());
    }

    @Test
    void testUseThrowsWhenThePlayerTargetsThemselves() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);

        assertThrows(IllegalArgumentException.class,
            () -> engine.use(FIRST_PLAYER_ID, FIRST_PLAYER_ID),
            "GameEngineImpl should throw when a player attacks themselves");
    }

    @Test
    void testUseThrowsWhenTheSelectedItemIsOfAnUnknownKind() {
        players.put(FIRST_PLAYER_ID, player);
        mockSelected(unknownItem);

        assertThrows(IllegalStateException.class, () -> engine.use(FIRST_PLAYER_ID, null),
            "GameEngineImpl should throw when the selected item is neither a weapon, a spell nor a potion");
    }

    @Test
    void testUseTakesTheKilledMinionOffTheMap() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();
        mockKilledMinion();

        engine.use(FIRST_PLAYER_ID, FIRST_MINION_ID);

        verify(map).removeActor(FIRST_MINION_ID);
    }

    @Test
    void testUseAwardsTheExperienceOfTheKilledMinion() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();
        mockKilledMinion();

        engine.use(FIRST_PLAYER_ID, FIRST_MINION_ID);

        verify(player).gainExperience(MINION_XP);
    }

    @Test
    void testUseSpawnsAnotherMinionWhenOneIsKilled() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();
        mockKilledMinion();
        when(map.randomFreePosition(random)).thenReturn(Optional.of(FREE_POSITION));
        when(random.nextInt(anyInt())).thenReturn(FIRST_SLOT);
        when(minionIds.acquire()).thenReturn(SECOND_MINION_ID);

        engine.use(FIRST_PLAYER_ID, FIRST_MINION_ID);

        verify(map).addActor(any(Minion.class));
    }

    @Test
    void testUseRespawnsTheKilledPlayerOnTheSpawnPoint() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();
        mockKilledPlayer();
        mockSpawnPoint();

        engine.use(FIRST_PLAYER_ID, SECOND_PLAYER_ID);

        verify(otherPlayerStats).restore();
        verify(otherPlayer).moveTo(SPAWN_POINT);
    }

    @Test
    void testUseDropsAnItemOfTheKilledPlayer() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();
        mockKilledPlayer();
        mockSpawnPoint();
        when(otherBackpack.slots()).thenReturn(List.of(SWORD));
        when(random.nextInt(anyInt())).thenReturn(FIRST_SLOT);
        when(treasureIds.acquire()).thenReturn(TREASURE_ID);

        engine.use(FIRST_PLAYER_ID, SECOND_PLAYER_ID);

        verify(map).addTreasure(any(DroppedTreasure.class));
        verify(otherBackpack).remove(FIRST_SLOT);
    }

    @Test
    void testPickUpPutsTheItemOfTheTreasureInTheBackpack() {
        players.put(FIRST_PLAYER_ID, player);
        mockTreasureOnTheSameTile();
        when(player.getBackpack()).thenReturn(backpack);

        engine.pickUp(FIRST_PLAYER_ID, TREASURE_ID);

        verify(backpack).add(SWORD);
    }

    @Test
    void testPickUpTakesTheTreasureOffTheMap() {
        players.put(FIRST_PLAYER_ID, player);
        mockTreasureOnTheSameTile();
        when(player.getBackpack()).thenReturn(backpack);

        engine.pickUp(FIRST_PLAYER_ID, TREASURE_ID);

        verify(map).removeTreasure(TREASURE_ID);
    }

    @Test
    void testPickUpAwardsTheExperienceOfTheTreasure() {
        players.put(FIRST_PLAYER_ID, player);
        mockTreasureOnTheSameTile();
        when(player.getBackpack()).thenReturn(backpack);
        when(treasure.getXp()).thenReturn(TREASURE_XP);

        engine.pickUp(FIRST_PLAYER_ID, TREASURE_ID);

        verify(player).gainExperience(TREASURE_XP);
    }

    @Test
    void testPickUpTellsOthersThePlayerReachedANewLevel() {
        players.put(FIRST_PLAYER_ID, player);
        players.put(SECOND_PLAYER_ID, otherPlayer);
        mockTreasureOnTheSameTile();
        when(player.getBackpack()).thenReturn(backpack);
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);
        when(player.getLevel()).thenReturn(level);
        when(level.getValue()).thenReturn(SECOND_LEVEL);
        when(treasure.getXp()).thenReturn(TREASURE_XP);
        when(player.gainExperience(TREASURE_XP)).thenReturn(1);

        List<GameEvent> events = engine.pickUp(FIRST_PLAYER_ID, TREASURE_ID);

        assertEquals(1, events.size(), "GameEngineImpl should report one event when a player reaches a level");
        assertEquals(Set.of(SECOND_PLAYER_ID), events.getFirst().recipients(),
            "GameEngineImpl should tell everyone but the player who reached the level");
    }

    @Test
    void testPickUpReportsNoEventsWhenNoLevelIsReached() {
        players.put(FIRST_PLAYER_ID, player);
        mockTreasureOnTheSameTile();
        when(player.getBackpack()).thenReturn(backpack);

        List<GameEvent> events = engine.pickUp(FIRST_PLAYER_ID, TREASURE_ID);

        assertTrue(events.isEmpty(),
            "GameEngineImpl should report no events when a picked up treasure brings no new level");
    }

    @Test
    void testPickUpThrowsWhenTheTreasureLiesOnAnotherTile() {
        players.put(FIRST_PLAYER_ID, player);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(map.getTreasure(TREASURE_ID)).thenReturn(Optional.of(treasure));
        when(treasure.getPosition()).thenReturn(FREE_POSITION);

        assertThrows(TargetNotReachableException.class,
            () -> engine.pickUp(FIRST_PLAYER_ID, TREASURE_ID),
            "GameEngineImpl should throw when the treasure lies on another tile");
        verify(map, never()).removeTreasure(anyInt());
    }

    @Test
    void testPickUpThrowsWhenTheTreasureIsNotOnTheMap() {
        players.put(FIRST_PLAYER_ID, player);
        when(map.getTreasure(UNKNOWN_TREASURE_ID)).thenReturn(Optional.empty());

        assertThrows(TargetNotReachableException.class,
            () -> engine.pickUp(FIRST_PLAYER_ID, UNKNOWN_TREASURE_ID),
            "GameEngineImpl should throw when the treasure is no longer on the map");
    }

    @Test
    void testPickUpThrowsWhenThePlayerIsNotInTheGame() {
        assertThrows(IllegalStateException.class,
            () -> engine.pickUp(UNKNOWN_PLAYER_ID, TREASURE_ID),
            "GameEngineImpl should throw when a player that is not in the game picks up a treasure");
    }

    @Test
    void testGivePutsTheItemInTheBackpackOfTheOtherPlayer() {
        players.put(FIRST_PLAYER_ID, player);
        players.put(SECOND_PLAYER_ID, otherPlayer);
        mockOtherPlayerOnTheSameTile();
        mockSelected(SWORD);
        when(otherPlayer.getBackpack()).thenReturn(otherBackpack);

        engine.give(FIRST_PLAYER_ID, SECOND_PLAYER_ID);

        verify(otherBackpack).add(SWORD);
    }

    @Test
    void testGiveTakesTheItemOutOfTheBackpackOfTheGiver() {
        players.put(FIRST_PLAYER_ID, player);
        players.put(SECOND_PLAYER_ID, otherPlayer);
        mockOtherPlayerOnTheSameTile();
        mockSelected(SWORD);
        when(otherPlayer.getBackpack()).thenReturn(otherBackpack);

        engine.give(FIRST_PLAYER_ID, SECOND_PLAYER_ID);

        verify(backpack).remove(FIRST_SLOT);
    }

    @Test
    void testGiveTellsOnlyThePlayerWhoGotTheItem() {
        players.put(FIRST_PLAYER_ID, player);
        players.put(SECOND_PLAYER_ID, otherPlayer);
        mockOtherPlayerOnTheSameTile();
        mockSelected(SWORD);
        when(otherPlayer.getBackpack()).thenReturn(otherBackpack);

        List<GameEvent> events = engine.give(FIRST_PLAYER_ID, SECOND_PLAYER_ID);

        assertEquals(1, events.size(), "GameEngineImpl should report one event when a player gives an item");
        assertEquals(Set.of(SECOND_PLAYER_ID), events.getFirst().recipients(),
            "GameEngineImpl should tell only the player who got the item");
    }

    @Test
    void testGiveThrowsWhenThePlayerGivesToThemselves() {
        players.put(FIRST_PLAYER_ID, player);
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);

        assertThrows(IllegalArgumentException.class,
            () -> engine.give(FIRST_PLAYER_ID, FIRST_PLAYER_ID),
            "GameEngineImpl should throw when a player gives an item to themselves");
    }

    @Test
    void testGiveThrowsWhenTheOtherPlayerStandsOnAnotherTile() {
        players.put(FIRST_PLAYER_ID, player);
        players.put(SECOND_PLAYER_ID, otherPlayer);
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(otherPlayer.getPosition()).thenReturn(FREE_POSITION);

        assertThrows(TargetNotReachableException.class,
            () -> engine.give(FIRST_PLAYER_ID, SECOND_PLAYER_ID),
            "GameEngineImpl should throw when the other player stands on another tile");
        verify(otherBackpack, never()).add(any());
    }

    @Test
    void testGiveThrowsWhenTheOtherPlayerIsNotInTheGame() {
        players.put(FIRST_PLAYER_ID, player);
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);

        assertThrows(TargetNotReachableException.class,
            () -> engine.give(FIRST_PLAYER_ID, UNKNOWN_PLAYER_ID),
            "GameEngineImpl should throw when the other player is not in the game");
    }

    @Test
    void testGiveThrowsWhenTheSelectedSlotIsEmpty() {
        players.put(FIRST_PLAYER_ID, player);
        players.put(SECOND_PLAYER_ID, otherPlayer);
        mockOtherPlayerOnTheSameTile();
        mockNothingSelected();

        assertThrows(EmptySlotException.class,
            () -> engine.give(FIRST_PLAYER_ID, SECOND_PLAYER_ID),
            "GameEngineImpl should throw when a player gives from an empty slot");
        verify(otherBackpack, never()).add(any());
    }

    @Test
    void testGiveThrowsWhenThePlayerIsNotInTheGame() {
        assertThrows(IllegalStateException.class,
            () -> engine.give(UNKNOWN_PLAYER_ID, FIRST_PLAYER_ID),
            "GameEngineImpl should throw when a player that is not in the game gives an item");
    }

    @Test
    void testDropLeavesTheItemOnTheTileOfThePlayer() {
        players.put(FIRST_PLAYER_ID, player);
        mockItemToDrop();

        engine.drop(FIRST_PLAYER_ID);

        ArgumentCaptor<Treasure> dropped = ArgumentCaptor.forClass(Treasure.class);
        verify(map).addTreasure(dropped.capture());

        assertInstanceOf(DroppedTreasure.class, dropped.getValue(),
            "GameEngineImpl should leave a dropped item as a dropped treasure");
        assertEquals(TREASURE_ID, dropped.getValue().getId(),
            "GameEngineImpl should give a dropped treasure the id it took from the generator");
        assertEquals(SPAWN_POINT, dropped.getValue().getPosition(),
            "GameEngineImpl should leave a dropped treasure on the tile of the player");
        assertEquals(SWORD, dropped.getValue().getItem(),
            "GameEngineImpl should leave the selected item in the dropped treasure");
    }

    @Test
    void testDropTakesTheItemOutOfTheBackpack() {
        players.put(FIRST_PLAYER_ID, player);
        mockItemToDrop();

        engine.drop(FIRST_PLAYER_ID);

        verify(backpack).remove(FIRST_SLOT);
    }

    @Test
    void testDropReportsNoEvents() {
        players.put(FIRST_PLAYER_ID, player);
        mockItemToDrop();

        List<GameEvent> events = engine.drop(FIRST_PLAYER_ID);

        assertTrue(events.isEmpty(), "GameEngineImpl should report no events when a player drops an item");
    }

    @Test
    void testDropThrowsWhenTheSelectedSlotIsEmpty() {
        players.put(FIRST_PLAYER_ID, player);
        mockNothingSelected();

        assertThrows(EmptySlotException.class, () -> engine.drop(FIRST_PLAYER_ID),
            "GameEngineImpl should throw when a player drops from an empty slot");
        verify(map, never()).addTreasure(any());
    }

    @Test
    void testDropThrowsWhenThePlayerIsNotInTheGame() {
        assertThrows(IllegalStateException.class, () -> engine.drop(UNKNOWN_PLAYER_ID),
            "GameEngineImpl should throw when a player that is not in the game drops an item");
    }

    private void mockItemToDrop() {
        mockSelected(SWORD);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(treasureIds.acquire()).thenReturn(TREASURE_ID);
    }

    private void mockOtherPlayerOnTheSameTile() {
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(otherPlayer.getPosition()).thenReturn(SPAWN_POINT);
    }

    private void mockTreasureOnTheSameTile() {
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(map.getTreasure(TREASURE_ID)).thenReturn(Optional.of(treasure));
        when(treasure.getPosition()).thenReturn(SPAWN_POINT);
        when(treasure.getItem()).thenReturn(SWORD);
    }

    private void mockKilledMinion() {
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(player.getStats()).thenReturn(playerStats);
        when(playerStats.getAttack()).thenReturn(PLAYER_ATTACK);
        when(map.getActor(FIRST_MINION_ID)).thenReturn(Optional.of(minion));
        when(minion.getId()).thenReturn(FIRST_MINION_ID);
        when(minion.getPosition()).thenReturn(SPAWN_POINT);
        when(minion.getStats()).thenReturn(targetStats);
        when(minion.getXpReward()).thenReturn(MINION_XP);
        when(minion.isAlive()).thenReturn(true, false);
        when(targetStats.getDefense()).thenReturn(TARGET_DEFENSE);
    }

    private void mockKilledPlayer() {
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(player.getStats()).thenReturn(playerStats);
        when(playerStats.getAttack()).thenReturn(PLAYER_ATTACK);
        when(map.getActor(SECOND_PLAYER_ID)).thenReturn(Optional.of(otherPlayer));
        when(otherPlayer.getPosition()).thenReturn(SPAWN_POINT);
        when(otherPlayer.getStats()).thenReturn(otherPlayerStats);
        when(otherPlayer.getBackpack()).thenReturn(otherBackpack);
        when(otherPlayer.isAlive()).thenReturn(true, false);
        when(otherPlayerStats.getDefense()).thenReturn(TARGET_DEFENSE);
    }

    private void mockTargetOnTheSameTile() {
        when(player.getId()).thenReturn(FIRST_PLAYER_ID);
        when(player.getPosition()).thenReturn(SPAWN_POINT);
        when(map.getActor(FIRST_MINION_ID)).thenReturn(Optional.of(target));
        when(target.isAlive()).thenReturn(true);
        when(target.getPosition()).thenReturn(SPAWN_POINT);
    }

    private void mockTargetDefense(int defense) {
        when(target.getStats()).thenReturn(targetStats);
        when(targetStats.getDefense()).thenReturn(defense);
    }

    private void mockNothingSelected() {
        when(player.getBackpack()).thenReturn(backpack);
        when(player.getSelectedSlot()).thenReturn(FIRST_SLOT);
        when(backpack.at(FIRST_SLOT)).thenReturn(Optional.empty());
    }

    private void mockSelected(Item item) {
        when(player.getBackpack()).thenReturn(backpack);
        when(player.getSelectedSlot()).thenReturn(FIRST_SLOT);
        when(backpack.at(FIRST_SLOT)).thenReturn(Optional.of(item));
    }

    private void mockPlayerLevel(int value) {
        when(player.getLevel()).thenReturn(level);
        when(level.getValue()).thenReturn(value);
    }

    private void mockSpawnPoint() {
        when(map.getTerrainGrid()).thenReturn(terrain);
        when(terrain.getSpawnPoint()).thenReturn(SPAWN_POINT);
    }

}
