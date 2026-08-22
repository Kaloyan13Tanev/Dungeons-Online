package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.JLineConsole;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.input.InputLoop;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.BackpackRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRendererImpl;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.ItemFormatter;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.MapRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.MessageRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.SelectionRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.StatsRenderer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class PlayerClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 4444;

    private static final int PANEL_COLUMN = 137;

    private static final String LOGGING_CONFIG = "/client-logging.properties";
    private static final Path LOG_DIRECTORY = Path.of("logs");
    private static final String CONTACT_ADMIN = "contact the administrator with the logs in ";

    private static final Logger LOGGER = Logger.getLogger(PlayerClient.class.getName());

    void main() {
        configureLogging();

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            LOGGER.log(Level.CONFIG, "Connected to {0}:{1}.", new Object[]{SERVER_HOST, SERVER_PORT});
            play(socket);
        } catch (UnknownHostException e) {
            LOGGER.log(Level.SEVERE, "Could not resolve host " + SERVER_HOST + ".", e);
            System.out.println("Cannot find the server at " + SERVER_HOST + ". " + contactAdmin());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "The session ended with an I/O error.", e);
            System.out.println("Unable to reach the server. Try again later, or " + contactAdmin());
        }
    }

    private void play(Socket socket) throws IOException {
        Console console = new JLineConsole();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            ClientState state = new ClientState();
            GameRenderer renderer = buildRenderer(console, state);
            RequestSender sender = new RequestSender(writer);

            ServerListener listener = new ServerListener(reader, renderer);
            Thread listenerThread = Thread.ofVirtual().start(listener);

            new InputLoop(console, state, renderer, sender).run();

            listener.stop();
            listenerThread.interrupt();
        } finally {
            console.clearScreen();
            console.close();
        }
    }

    private GameRenderer buildRenderer(Console console, ClientState state) {
        ItemFormatter items = new ItemFormatter();

        return new GameRendererImpl(console, state,
                new MapRenderer(console, items),
                new StatsRenderer(console, PANEL_COLUMN),
                new BackpackRenderer(console, items, PANEL_COLUMN),
                new MessageRenderer(console, PANEL_COLUMN),
                new SelectionRenderer(console, items, PANEL_COLUMN));
    }

    private void configureLogging() {
        try (InputStream config = PlayerClient.class.getResourceAsStream(LOGGING_CONFIG)) {
            Files.createDirectories(LOG_DIRECTORY);

            if (config == null) {
                System.out.println("Missing " + LOGGING_CONFIG + ". Errors will not be recorded.");
                return;
            }

            LogManager.getLogManager().readConfiguration(config);
        } catch (IOException e) {
            System.out.println("Could not set up logging. Errors will not be recorded.");
        }
    }

    private String contactAdmin() {
        return CONTACT_ADMIN + LOG_DIRECTORY.toAbsolutePath() + ".";
    }

}
