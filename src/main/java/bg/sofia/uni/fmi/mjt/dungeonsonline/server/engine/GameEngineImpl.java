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
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.DroppedTreasure;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.IdGenerator;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameEngineImpl implements GameEngine {

    private static final Logger LOGGER = Logger.getLogger(GameEngineImpl.class.getName());

    private static final int DEFENSE_DIVISOR = 2;
    private static final int NO_DAMAGE = 0;

    private static final int MIN_MINION_LEVEL = 1;
    private static final int MAX_MINION_LEVEL = 5;

    private final GameMap map;
    private final IdGenerator<Integer> treasureIds;
    private final IdGenerator<Integer> minionIds;
    private final Random random;
    private final Map<Integer, Player> players;

    public GameEngineImpl(GameMap map, IdGenerator<Integer> treasureIds,
                          IdGenerator<Integer> minionIds, Random random) {
        this.map = map;
        this.treasureIds = treasureIds;
        this.minionIds = minionIds;
        this.random = random;
        this.players = new HashMap<>();
    }

    @Override
    public synchronized void join(int playerId) {
        if (players.containsKey(playerId)) {
            throw new IllegalStateException("Player " + playerId + " is already in the game");
        }

        Player player = new Player(playerId, map.getTerrainGrid().getSpawnPoint());
        map.addActor(player);
        players.put(playerId, player);

        LOGGER.log(Level.INFO, "Player {0} joined at {1}.",
            new Object[] {playerId, player.getPosition()});
    }

    @Override
    public synchronized void leave(int playerId) {
        Player player = players.remove(playerId);
        if (player == null) {
            LOGGER.log(Level.FINE, "Nothing to remove for player {0}.", playerId);
            return;
        }

        map.removeActor(playerId);
        LOGGER.log(Level.INFO, "Player {0} left the game.", playerId);
    }

    @Override
    public synchronized void move(int playerId, Direction direction) {
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
    }

    @Override
    public synchronized void select(int playerId, int slot) {
        requirePlayer(playerId).select(slot);
        LOGGER.log(Level.FINE, "Player {0} selected slot {1}.", new Object[] {playerId, slot});
    }

    @Override
    public synchronized void use(int playerId, Integer targetId) {
        Player player = requirePlayer(playerId);
        Item selected = player.getBackpack().at(player.getSelectedSlot()).orElse(null);

        switch (selected) {
            case Spell spell -> cast(player, spell);
            case Potion potion -> drink(player, potion);
            case Weapon weapon -> attack(player, requireTarget(player, targetId), weapon);
            case null -> attack(player, requireTarget(player, targetId), null);
        }
    }

    @Override
    public synchronized void pickUp(int playerId, int treasureId) {
        Player player = requirePlayer(playerId);
        Treasure treasure = map.getTreasure(treasureId)
            .filter(found -> found.getPosition().equals(player.getPosition()))
            .orElseThrow(() -> new TargetNotReachableException("That treasure is not here anymore!"));

        player.getBackpack().add(treasure.getItem());
        map.removeTreasure(treasureId);
        player.gainExperience(treasure.getXp());

        LOGGER.log(Level.INFO, "Player {0} picked up {1} at {2}.",
            new Object[] {playerId, treasure.getItem().name(), player.getPosition()});
    }

    @Override
    public synchronized void give(int playerId, int targetPlayerId) {
        Player player = requirePlayer(playerId);
        Player target = requirePlayerOnTheSameTile(player, targetPlayerId);

        int slot = player.getSelectedSlot();
        Item item = player.getBackpack().at(slot)
            .orElseThrow(() -> new EmptySlotException("You have nothing to give!"));

        target.getBackpack().add(item);
        player.getBackpack().remove(slot);

        LOGGER.log(Level.INFO, "Player {0} gave {1} to player {2}.",
            new Object[] {playerId, item.name(), targetPlayerId});
    }

    @Override
    public synchronized void drop(int playerId) {
        Player player = requirePlayer(playerId);

        int slot = player.getSelectedSlot();
        Item item = player.getBackpack().at(slot)
            .orElseThrow(() -> new EmptySlotException("You have nothing to drop!"));

        map.addTreasure(new DroppedTreasure(treasureIds.acquire(), player.getPosition(), item));
        player.getBackpack().remove(slot);

        LOGGER.log(Level.INFO, "Player {0} dropped {1} at {2}.",
            new Object[] {playerId, item.name(), player.getPosition()});
    }

    private Player requirePlayer(int playerId) {
        Player player = players.get(playerId);
        if (player == null) {
            throw new IllegalStateException("Player " + playerId + " is not in the game");
        }

        return player;
    }

    private void attack(Player player, Actor target, Weapon weapon) {
        int power = player.getStats().getAttack();

        if (weapon != null) {
            requireLevel(player, weapon.level(), weapon.name());
            power += weapon.attack();
        }

        strike(player, target, power);
    }

    private void cast(Player player, Spell spell) {
        requireLevel(player, spell.level(), spell.name());
        player.getStats().spendMana(spell.manaCost());

        for (Actor target : map.actorsAt(player.getPosition())) {
            if (target.getId() != player.getId() && target.isAlive()) {
                strike(player, target, spell.damage());
            }
        }
    }

    private void drink(Player player, Potion potion) {
        potion.applyTo(player.getStats());
        player.getBackpack().remove(player.getSelectedSlot());

        LOGGER.log(Level.INFO, "Player {0} used {1}.",
            new Object[] {player.getId(), potion.name()});
    }

    private void strike(Player player, Actor target, int power) {
        int damage = Math.max(NO_DAMAGE, power - target.getStats().getDefense() / DEFENSE_DIVISOR);
        target.getStats().takeDamage(damage);

        LOGGER.log(Level.INFO, "Player {0} hit actor {1} for {2}.",
            new Object[] {player.getId(), target.getId(), damage});

        if (!target.isAlive()) {
            kill(player, target);
        }
    }

    private void kill(Player player, Actor target) {
        LOGGER.log(Level.INFO, "Player {0} killed actor {1}.",
            new Object[] {player.getId(), target.getId()});

        switch (target) {
            case Minion minion -> {
                map.removeActor(minion.getId());
                player.gainExperience(minion.getXpReward());
                spawnMinion();
            }
            case Player dead -> respawn(dead);
            default -> throw new IllegalStateException("Unknown actor " + target.getId());
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

    private static void requireLevel(Player player, int level, String name) {
        if (player.getLevel().getValue() < level) {
            throw new ItemLevelTooHighException("You are not experienced enough to use " + name + "!");
        }
    }

    private Actor requireTarget(Player player, Integer targetId) {
        if (targetId == null) {
            throw new TargetNotReachableException("Choose a target first!");
        }

        if (targetId == player.getId()) {
            throw new IllegalArgumentException("Player " + player.getId() + " cannot target themselves");
        }

        return map.getActor(targetId)
            .filter(Actor::isAlive)
            .filter(actor -> actor.getPosition().equals(player.getPosition()))
            .orElseThrow(() -> new TargetNotReachableException("That target is not here anymore!"));
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

    private static Position step(Position from, Direction direction) {
        return switch (direction) {
            case UP -> new Position(from.row() - 1, from.col());
            case DOWN -> new Position(from.row() + 1, from.col());
            case LEFT -> new Position(from.row(), from.col() - 1);
            case RIGHT -> new Position(from.row(), from.col() + 1);
        };
    }
}
