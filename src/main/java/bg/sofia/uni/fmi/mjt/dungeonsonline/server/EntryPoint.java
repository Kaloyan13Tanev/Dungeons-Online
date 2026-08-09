package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngine;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngineImpl;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.MinionSpawn;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.TreasureSpawn;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Spell;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.Weapon;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.potion.ManaPotion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.Terrain;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.TerrainGrid;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.RequestHandler;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.IdPool;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.SequentialIdGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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

    private static final List<MinionSpawn> MINIONS = List.of(
        new MinionSpawn(new Position(1, 8), 1),
        new MinionSpawn(new Position(2, 1), 1),
        new MinionSpawn(new Position(4, 5), 2),
        new MinionSpawn(new Position(5, 9), 2),
        new MinionSpawn(new Position(7, 2), 3),
        new MinionSpawn(new Position(8, 7), 3),
        new MinionSpawn(new Position(10, 4), 4),
        new MinionSpawn(new Position(9, 10), 5));

    private static final List<TreasureSpawn> TREASURES = List.of(
        new TreasureSpawn(new Position(0, 6), new Weapon("Sword", 1, 20)),
        new TreasureSpawn(new Position(2, 9), new Weapon("Axe", 2, 35)),
        new TreasureSpawn(new Position(10, 0), new Weapon("Excalibur", 4, 70)),
        new TreasureSpawn(new Position(1, 1), new Spell("Spark", 1, 30, 20)),
        new TreasureSpawn(new Position(6, 9), new Spell("Fireball", 2, 50, 40)),
        new TreasureSpawn(new Position(3, 0), new HealthPotion("Bandage", 30)),
        new TreasureSpawn(new Position(8, 3), new HealthPotion("Elixir of life", 60)),
        new TreasureSpawn(new Position(4, 10), new ManaPotion("Mana flask", 30)),
        new TreasureSpawn(new Position(9, 6), new ManaPotion("Arcane brew", 60)),
        new TreasureSpawn(new Position(5, 2), new HealthPotion("Bandage", 30)));

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
        GameEngine engine = new GameEngineImpl(
            new GameMap(new TerrainGrid(MAP, SPAWN_POINT)),
            new SequentialIdGenerator(),
            new SequentialIdGenerator(PLAYER_COUNT + 1),
            new Random());
        engine.populate(MINIONS, TREASURES);

        RequestHandler handler = new RequestHandler(registry, engine);
        try {
            GameServer server = new GameServer(pool, registry, handler, engine);
            server.run();
        } catch (IOException e) {
            System.out.println("Could not start the server.");
            LOGGER.log(Level.SEVERE, "Error occurred while starting the server.", e);
        }
    }

}
