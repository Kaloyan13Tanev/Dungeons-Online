package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngine;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngineImpl;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.Terrain;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.TerrainGrid;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.RequestHandler;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.id.IdPool;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class EntryPoint {

    private static final int PLAYER_COUNT = 9;
    private static final Logger LOGGER = Logger.getLogger(EntryPoint.class.getName());
    private static final String LOGGING_CONFIG = "/server-logging.properties";

    private static final char OBSTACLE = '#';

    private static final String[] MAP = {
        "..##......",
        "...#...##.",
        ".....#....",
        ".#...#...#",
        "..........",
    };

    private static final Position SPAWN_POINT = new Position(4, 0);

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
        GameEngine engine = new GameEngineImpl(new GameMap(new TerrainGrid(terrain(), SPAWN_POINT)));
        RequestHandler handler = new RequestHandler(registry, engine);
        try {
            GameServer server = new GameServer(pool, registry, handler, engine);
            server.run();
        } catch (IOException e) {
            System.out.println("Could not start the server.");
            LOGGER.log(Level.SEVERE, "Error occurred while starting the server.", e);
        }
    }

    private static Terrain[][] terrain() {
        Terrain[][] grid = new Terrain[MAP.length][];

        for (int row = 0; row < MAP.length; row++) {
            grid[row] = new Terrain[MAP[row].length()];

            for (int col = 0; col < MAP[row].length(); col++) {
                grid[row][col] = MAP[row].charAt(col) == OBSTACLE ? Terrain.OBSTACLE : Terrain.GROUND;
            }
        }

        return grid;
    }
}