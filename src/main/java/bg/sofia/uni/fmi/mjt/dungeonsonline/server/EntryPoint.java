package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.pool.IdPool;

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
        RequestHandler handler = new RequestHandler(registry);
        try {
            GameServer server = new GameServer(pool, registry, handler);
            server.run();
        } catch (IOException e) {
            System.out.println("Could not start the server.");
            LOGGER.log(Level.SEVERE, "Error occurred while starting the server.", e);
        }
    }
}