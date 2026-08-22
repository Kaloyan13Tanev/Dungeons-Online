package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Minion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.EmptySlotException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.ItemLevelTooHighException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.Potion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Spell;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Weapon;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.InvalidMoveException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.TerrainGrid;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state.GameStateProducer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.DroppedTreasure;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.IdGenerator;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameEngineImpl implements GameEngine {

    private static final Logger LOGGER = Logger.getLogger(GameEngineImpl.class.getName());

    private static final int DEFENSE_EFFICIENCY = 2;
    private static final int NO_DAMAGE = 0;

    private static final int MIN_MINION_LEVEL = 1;
    private static final int MAX_MINION_LEVEL = 5;

    private static final String MINION_NAME = "a minion";

    private final GameMap map;
    private final IdGenerator<Integer> treasureIds;
    private final IdGenerator<Integer> minionIds;
    private final Random random;
    private final Map<Integer, Player> players;
    private final GameStateProducer stateProducer;

    public GameEngineImpl(GameMap map, IdGenerator<Integer> treasureIds,
                          IdGenerator<Integer> minionIds, Random random) {
        this(map, treasureIds, minionIds, random, new HashMap<>(), new GameStateProducer());
    }

    public GameEngineImpl(GameMap map, IdGenerator<Integer> treasureIds,
                          IdGenerator<Integer> minionIds, Random random, Map<Integer, Player> players) {
        this(map, treasureIds, minionIds, random, players, new GameStateProducer());
    }

    public GameEngineImpl(GameMap map, IdGenerator<Integer> treasureIds, IdGenerator<Integer> minionIds,
                          Random random, Map<Integer, Player> players, GameStateProducer stateProducer) {
        this.map = requireNotNull(map, "Map");
        this.treasureIds = requireNotNull(treasureIds, "Treasure id generator");
        this.minionIds = requireNotNull(minionIds, "Minion id generator");
        this.random = requireNotNull(random, "Random");
        this.players = players;
        this.stateProducer = stateProducer;
    }

    @Override
    public synchronized TerrainDTO terrain() {
        return stateProducer.terrainOf(map);
    }

    @Override
    public synchronized Map<Integer, GameStateDTO> stateForAll() {
        return stateProducer.stateForAll(map, players.values());
    }

    @Override
    public synchronized List<GameEvent> join(int playerId) {
        if (players.containsKey(playerId)) {
            throw new IllegalStateException("Player " + playerId + " is already in the game");
        }

        Player player = new Player(playerId, map.getTerrainGrid().getSpawnPoint());
        map.addActor(player);
        players.put(playerId, player);

        LOGGER.log(Level.INFO, "Player {0} joined at {1}.",
            new Object[] {playerId, player.getPosition()});

        return List.of(new GameEvent(everyoneExcept(playerId), name(playerId) + " joined the game."));
    }

    @Override
    public synchronized List<GameEvent> leave(int playerId) {
        Player player = players.remove(playerId);
        if (player == null) {
            LOGGER.log(Level.FINE, "Nothing to remove for player {0}.", playerId);
            return List.of();
        }

        map.removeActor(playerId);
        LOGGER.log(Level.INFO, "Player {0} left the game.", playerId);

        return List.of(new GameEvent(everyone(), name(playerId) + " left the game."));
    }

    @Override
    public synchronized List<GameEvent> move(int playerId, Direction direction) {
        Player player = requirePlayer(playerId);
        Position target = step(player.getPosition(), direction);
        TerrainGrid terrain = map.getTerrainGrid();

        if (!terrain.isInside(target)) {
            throw new InvalidMoveException("You cannot leave the map!");
        }

        if (!terrain.isWalkable(target)) {
            throw new InvalidMoveException("There is something in your way!");
        }

        player.moveTo(target);
        LOGGER.log(Level.FINE, "Player {0} moved {1} to {2}.",
            new Object[] {playerId, direction, target});

        return List.of();
    }

    @Override
    public synchronized List<GameEvent> select(int playerId, int slot) {
        requirePlayer(playerId).select(slot);
        LOGGER.log(Level.FINE, "Player {0} selected slot {1}.", new Object[] {playerId, slot});

        return List.of();
    }

    @Override
    public synchronized List<GameEvent> use(int playerId, Integer targetId) {
        Player player = requirePlayer(playerId);
        Item selected = player.getBackpack().at(player.getSelectedSlot()).orElse(null);
        List<GameEvent> events = new ArrayList<>();

        switch (selected) {
            case Spell spell -> cast(player, spell, events);
            case Potion potion -> drink(player, potion);
            case Weapon weapon -> attack(player, requireTarget(player, targetId), weapon, events);
            case null -> attack(player, requireTarget(player, targetId), null, events);
            default -> throw new IllegalStateException("Unknown item " + selected.name());
        }

        return events;
    }

    @Override
    public synchronized List<GameEvent> pickUp(int playerId, int treasureId) {
        Player player = requirePlayer(playerId);
        Treasure treasure = map.getTreasure(treasureId)
            .filter(found -> found.getPosition().equals(player.getPosition()))
            .orElseThrow(() -> new TargetNotReachableException("That treasure is not here anymore!"));

        player.getBackpack().add(treasure.getItem());
        map.removeTreasure(treasureId);

        List<GameEvent> events = new ArrayList<>();
        award(player, treasure.getXp(), events);

        LOGGER.log(Level.INFO, "Player {0} picked up {1} at {2}.",
            new Object[] {playerId, treasure.getItem().name(), player.getPosition()});

        return events;
    }

    @Override
    public synchronized List<GameEvent> give(int playerId, int targetPlayerId) {
        Player player = requirePlayer(playerId);
        Player target = requirePlayerOnTheSameTile(player, targetPlayerId);

        int slot = player.getSelectedSlot();
        Item item = player.getBackpack().at(slot)
            .orElseThrow(() -> new EmptySlotException("You have nothing to give!"));

        target.getBackpack().add(item);
        player.getBackpack().remove(slot);

        LOGGER.log(Level.INFO, "Player {0} gave {1} to player {2}.",
            new Object[] {playerId, item.name(), targetPlayerId});

        return List.of(new GameEvent(Set.of(targetPlayerId),
            name(playerId) + " gave you " + item.name() + "."));
    }

    @Override
    public synchronized List<GameEvent> drop(int playerId) {
        Player player = requirePlayer(playerId);

        int slot = player.getSelectedSlot();
        Item item = player.getBackpack().at(slot)
            .orElseThrow(() -> new EmptySlotException("You have nothing to drop!"));

        map.addTreasure(new DroppedTreasure(treasureIds.acquire(), player.getPosition(), item));
        player.getBackpack().remove(slot);

        LOGGER.log(Level.INFO, "Player {0} dropped {1} at {2}.",
            new Object[] {playerId, item.name(), player.getPosition()});

        return List.of();
    }

    private Player requirePlayer(int playerId) {
        Player player = players.get(playerId);
        if (player == null) {
            throw new IllegalStateException("Player " + playerId + " is not in the game");
        }

        return player;
    }

    private void attack(Player player, Actor target, Weapon weapon, List<GameEvent> events) {
        int power = player.getStats().getAttack();

        if (weapon != null) {
            requireLevel(player, weapon.level(), weapon.name());
            power += weapon.attack();
        }

        strike(player, target, power, events);
    }

    private void cast(Player player, Spell spell, List<GameEvent> events) {
        requireLevel(player, spell.level(), spell.name());
        player.getStats().spendMana(spell.manaCost());

        for (Actor target : map.actorsAt(player.getPosition())) {
            if (target.getId() != player.getId() && target.isAlive()) {
                strike(player, target, spell.damage(), events);
            }
        }
    }

    private void drink(Player player, Potion potion) {
        potion.applyTo(player.getStats());
        player.getBackpack().remove(player.getSelectedSlot());

        LOGGER.log(Level.INFO, "Player {0} used {1}.",
            new Object[] {player.getId(), potion.name()});
    }

    private void strike(Player player, Actor target, int power, List<GameEvent> events) {
        int damage = Math.max(NO_DAMAGE, power - target.getStats().getDefense() / DEFENSE_EFFICIENCY);
        target.getStats().takeDamage(damage);

        LOGGER.log(Level.INFO, "Player {0} hit actor {1} for {2}.",
            new Object[] {player.getId(), target.getId(), damage});

        events.add(new GameEvent(everyone(),
            name(player.getId()) + " hit " + name(target) + " for " + damage + " damage."));

        if (!target.isAlive()) {
            kill(player, target, events);
        }
    }

    private void kill(Player player, Actor target, List<GameEvent> events) {
        LOGGER.log(Level.INFO, "Player {0} killed actor {1}.",
            new Object[] {player.getId(), target.getId()});

        events.add(new GameEvent(everyone(), name(target) + " died."));

        switch (target) {
            case Minion minion -> {
                map.removeActor(minion.getId());
                award(player, minion.getXpReward(), events);
                spawnMinion();
            }
            case Player dead -> respawn(dead);
            default -> throw new IllegalStateException("Unknown actor " + target.getId());
        }
    }

    private void award(Player player, int experience, List<GameEvent> events) {
        if (player.gainExperience(experience) > 0) {
            events.add(new GameEvent(everyoneExcept(player.getId()),
                name(player.getId()) + " reached level " + player.getLevel().getValue() + "."));
        }
    }

    private void spawnMinion() {
        Optional<Position> position = map.randomFreePosition(random);

        if (position.isEmpty()) {
            LOGGER.log(Level.WARNING, "No free position left for a new minion.");
            return;
        }

        int level = MIN_MINION_LEVEL + random.nextInt(MAX_MINION_LEVEL - MIN_MINION_LEVEL + 1);
        Minion minion = new Minion(minionIds.acquire(), level, position.get());
        map.addActor(minion);

        LOGGER.log(Level.INFO, "Minion {0} of level {1} spawned at {2}.",
            new Object[] {minion.getId(), level, position.get()});
    }

    private void respawn(Player dead) {
        dropRandomItem(dead);
        dead.getStats().restore();
        dead.moveTo(map.getTerrainGrid().getSpawnPoint());

        LOGGER.log(Level.INFO, "Player {0} respawned at {1}.",
            new Object[] {dead.getId(), dead.getPosition()});
    }

    private void dropRandomItem(Player player) {
        List<Item> slots = player.getBackpack().slots();
        List<Integer> occupied = new ArrayList<>();

        for (int slot = 0; slot < slots.size(); slot++) {
            if (slots.get(slot) != null) {
                occupied.add(slot);
            }
        }

        if (occupied.isEmpty()) {
            return;
        }

        int slot = occupied.get(random.nextInt(occupied.size()));
        Item item = slots.get(slot);

        map.addTreasure(new DroppedTreasure(treasureIds.acquire(), player.getPosition(), item));
        player.getBackpack().remove(slot);

        LOGGER.log(Level.INFO, "Player {0} dropped {1} on death.",
            new Object[] {player.getId(), item.name()});
    }

    private Set<Integer> everyone() {
        return Set.copyOf(players.keySet());
    }

    private Set<Integer> everyoneExcept(int playerId) {
        Set<Integer> recipients = new HashSet<>(players.keySet());
        recipients.remove(playerId);

        return Set.copyOf(recipients);
    }

    private static String name(int playerId) {
        return "Player " + playerId;
    }

    private static String name(Actor actor) {
        return actor instanceof Player ? name(actor.getId()) : MINION_NAME;
    }

    private static void requireLevel(Player player, int level, String name) {
        if (player.getLevel().getValue() < level) {
            throw new ItemLevelTooHighException("You are not experienced enough to use " + name + "!");
        }
    }

    private Actor requireTarget(Player player, Integer targetId) {
        if (targetId == null) {
            throw new TargetNotReachableException(hasTargets(player)
                ? "Choose a target first!"
                : "There is nothing to attack here!");
        }

        if (targetId == player.getId()) {
            throw new IllegalArgumentException("Player " + player.getId() + " cannot target themselves");
        }

        return map.getActor(targetId)
            .filter(Actor::isAlive)
            .filter(actor -> actor.getPosition().equals(player.getPosition()))
            .orElseThrow(() -> new TargetNotReachableException("That target is not here anymore!"));
    }

    private boolean hasTargets(Player player) {
        for (Actor actor : map.actorsAt(player.getPosition())) {
            if (actor.getId() != player.getId() && actor.isAlive()) {
                return true;
            }
        }

        return false;
    }

    private Player requirePlayerOnTheSameTile(Player player, int targetPlayerId) {
        if (targetPlayerId == player.getId()) {
            throw new IllegalArgumentException("Player " + player.getId() + " cannot target themselves");
        }

        Player target = players.get(targetPlayerId);
        if (target == null || !target.getPosition().equals(player.getPosition())) {
            throw new TargetNotReachableException("That player is not here anymore!");
        }

        return target;
    }

    private static <T> T requireNotNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }

        return value;
    }

    private static Position step(Position from, Direction direction) {
        return switch (direction) {
            case UP -> new Position(from.row() - 1, from.col());
            case DOWN -> new Position(from.row() + 1, from.col());
            case LEFT -> new Position(from.row(), from.col() - 1);
            case RIGHT -> new Position(from.row(), from.col() + 1);
        };
    }

}
