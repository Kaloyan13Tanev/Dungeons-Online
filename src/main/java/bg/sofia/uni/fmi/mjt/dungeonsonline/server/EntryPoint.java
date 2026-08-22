package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngine;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngineImpl;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Minion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Spell;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Weapon;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.ManaPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.Terrain;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.TerrainGrid;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.RequestHandler;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.IdGenerator;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.IdPool;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.SequentialIdGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class EntryPoint {

    private static final int PLAYER_COUNT = 9;
    private static final Logger LOGGER = Logger.getLogger(EntryPoint.class.getName());
    private static final String LOGGING_CONFIG = "/server-logging.properties";

    private static final Terrain G = Terrain.GROUND;
    private static final Terrain O = Terrain.OBSTACLE;

    private static final Terrain[][] MAP = {
        {G, G, G, G, G, G, G, G, G, G, G},
        {G, G, G, O, O, G, G, G, G, G, G},
        {G, G, G, O, O, O, G, G, G, G, G},
        {G, G, G, G, O, O, G, G, G, G, G},
        {G, G, G, G, G, G, G, G, G, G, G},
        {G, G, G, G, G, G, O, O, G, G, G},
        {G, G, G, G, G, O, O, O, G, G, G},
        {G, G, G, G, G, G, O, O, G, G, G},
        {G, G, G, G, G, G, G, G, G, G, G},
        {G, O, O, G, G, G, G, G, O, O, G},
        {G, G, G, G, G, G, G, G, G, G, G},
    };

    private static final Position SPAWN_POINT = new Position(0, 0);

    private static final Item SWORD = new Weapon("Sword", 1, 20);
    private static final Item AXE = new Weapon("Axe", 2, 35);
    private static final Item EXCALIBUR = new Weapon("Excalibur", 4, 70);
    private static final Item SPARK = new Spell("Spark", 1, 30, 20);
    private static final Item FIREBALL = new Spell("Fireball", 2, 50, 40);
    private static final Item BANDAGE = new HealthPotion("Bandage", 30);
    private static final Item ELIXIR_OF_LIFE = new HealthPotion("Elixir of life", 60);
    private static final Item MANA_FLASK = new ManaPotion("Mana flask", 30);
    private static final Item ARCANE_BREW = new ManaPotion("Arcane brew", 60);

    private static final Position[] MINION_POSITIONS = {
        new Position(1, 8), new Position(2, 1), new Position(4, 5), new Position(5, 9),
        new Position(7, 2), new Position(8, 7), new Position(10, 4), new Position(9, 10)
    };

    private static final int[] MINION_LEVELS = {1, 1, 2, 2, 3, 3, 4, 5};

    private static final Position[] TREASURE_POSITIONS = {
        new Position(0, 6), new Position(2, 9), new Position(10, 0), new Position(1, 1), new Position(6, 9),
        new Position(3, 0), new Position(8, 3), new Position(4, 10), new Position(9, 6), new Position(5, 2)
    };

    private static final Item[] TREASURE_ITEMS = {
        SWORD, AXE, EXCALIBUR, SPARK, FIREBALL, BANDAGE, ELIXIR_OF_LIFE, MANA_FLASK, ARCANE_BREW, BANDAGE
    };

    static {
        try (InputStream config = EntryPoint.class.getResourceAsStream(LOGGING_CONFIG)) {
            Files.createDirectories(Path.of("logs"));

            if (config == null) {
                System.out.println("Missing " + LOGGING_CONFIG + ". Errors will not be recorded.");
            } else {
                LogManager.getLogManager().readConfiguration(config);
            }
        } catch (IOException e) {
            System.out.println("Could not set up logging. Errors will not be recorded.");
        }
    }

    void main() {
        IdPool pool = new IdPool(PLAYER_COUNT);
        ConnectionRegistry registry = new ConnectionRegistry();

        IdGenerator<Integer> treasureIds = new SequentialIdGenerator();
        IdGenerator<Integer> minionIds = new SequentialIdGenerator(PLAYER_COUNT + 1);

        GameEngine engine = new GameEngineImpl(
            new GameMap(new TerrainGrid(MAP, SPAWN_POINT), minions(minionIds), treasures(treasureIds)),
            treasureIds,
            minionIds,
            new Random());

        RequestHandler handler = new RequestHandler(registry, engine);
        try {
            GameServer server = new GameServer(pool, registry, handler, engine);
            server.run();
        } catch (IOException e) {
            System.out.println("Could not start the server.");
            LOGGER.log(Level.SEVERE, "Error occurred while starting the server.", e);
        }
    }

    private static Map<Integer, Actor> minions(IdGenerator<Integer> ids) {
        Map<Integer, Actor> minions = new HashMap<>();

        for (int minion = 0; minion < MINION_POSITIONS.length; minion++) {
            Minion spawned = new Minion(ids.acquire(), MINION_LEVELS[minion], MINION_POSITIONS[minion]);
            minions.put(spawned.getId(), spawned);
        }

        return minions;
    }

    private static Map<Integer, Treasure> treasures(IdGenerator<Integer> ids) {
        Map<Integer, Treasure> treasures = new HashMap<>();

        for (int treasure = 0; treasure < TREASURE_POSITIONS.length; treasure++) {
            Treasure placed =
                new Treasure(ids.acquire(), TREASURE_POSITIONS[treasure], TREASURE_ITEMS[treasure]);
            treasures.put(placed.getId(), placed);
        }

        return treasures;
    }

}
