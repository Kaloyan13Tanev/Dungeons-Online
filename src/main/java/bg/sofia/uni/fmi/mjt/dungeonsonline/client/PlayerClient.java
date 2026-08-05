package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.RequestMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.QuitRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

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
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class PlayerClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 4444;

    private static final String LOGGING_CONFIG = "/client-logging.properties";
    private static final Path LOG_DIRECTORY = Path.of("logs");

    private static final String USAGE = "Commands: move up|down|left|right, quit";
    private static final String LOST_CONNECTION = "Lost the connection to the server.";
    private static final String PRESS_ENTER = " Press Enter to exit.";
    private static final String CONTACT_ADMIN = "contact the administrator with the logs in ";

    private static final Logger LOGGER = Logger.getLogger(PlayerClient.class.getName());

    private final RequestMapper mapper = new RequestMapper();
    private final AtomicBoolean playing = new AtomicBoolean();

    void main() {
        configureLogging();

        Socket socket = connect();
        if (socket == null) {
            return;
        }

        LOGGER.log(Level.CONFIG, "Connected to {0}:{1}.", new Object[] {SERVER_HOST, SERVER_PORT});
        start(socket);
    }

    private Socket connect() {
        try {
            return new Socket(SERVER_HOST, SERVER_PORT);
        } catch (UnknownHostException e) {
            LOGGER.log(Level.SEVERE, "Could not resolve host " + SERVER_HOST + ".", e);
            System.out.println("Cannot find the server at " + SERVER_HOST + ". " + contactAdmin());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not open a connection to " + SERVER_HOST + ":" + SERVER_PORT + ".", e);
            System.out.println("Unable to connect to the server. Try again later, or " + contactAdmin());
        }

        return null;
    }

    private void start(Socket socket) {
        playing.set(true);

        try (socket;
             BufferedWriter writer = new BufferedWriter(
                 new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            System.out.println(USAGE);

            Thread.ofVirtual().start(() -> listen(reader));
            send(writer, scanner);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "The session ended with an I/O error.", e);
            System.out.println(LOST_CONNECTION + " " + contactAdmin());
        } finally {
            playing.set(false);
            LOGGER.log(Level.INFO, "Session finished.");
        }
    }

    private void listen(BufferedReader reader) {
        try {
            String response;
            while ((response = reader.readLine()) != null) {
                System.out.println(response);
            }

            if (playing.get()) {
                LOGGER.log(Level.WARNING, "The server closed the connection.");
                System.out.println("The server closed the connection." + PRESS_ENTER);
            }
        } catch (IOException e) {
            if (playing.get()) {
                LOGGER.log(Level.WARNING, "Reading from the server failed.", e);
                System.out.println(LOST_CONNECTION + PRESS_ENTER);
            }
        }
    }

    private void send(BufferedWriter writer, Scanner scanner) throws IOException {
        while (playing.get() && scanner.hasNextLine()) {
            Optional<Request> parsed = parse(scanner.nextLine());
            if (parsed.isEmpty()) {
                System.out.println(USAGE);
                continue;
            }

            Request request = parsed.get();
            writer.write(mapper.serialize(request));
            writer.newLine();
            writer.flush();
            LOGGER.log(Level.INFO, "Sent {0} to the server.", request);

            if (request instanceof QuitRequest) {
                playing.set(false);
            }
        }
    }

    private Optional<Request> parse(String input) {
        return switch (input.trim().toLowerCase(Locale.ROOT)) {
            case "move up" -> Optional.of(new MoveRequest(Direction.UP));
            case "move down" -> Optional.of(new MoveRequest(Direction.DOWN));
            case "move left" -> Optional.of(new MoveRequest(Direction.LEFT));
            case "move right" -> Optional.of(new MoveRequest(Direction.RIGHT));
            case "quit" -> Optional.of(new QuitRequest());
            default -> Optional.empty();
        };
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
