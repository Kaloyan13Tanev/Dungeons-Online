package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.CancelCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.ClientCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.ConfirmCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.DropCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.HighlightCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.MoveCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.OpenListCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.QuitCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.SelectSlotCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.UseCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.JLineConsole;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.input.InputLoop;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.input.KeyBindings;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.input.KeyReader;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.BackpackRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRendererImpl;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.ItemFormatter;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.MapRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.MessageRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.SelectionRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.StatsRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.GiveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.PickUpRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.UseRequest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntFunction;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ClientEntryPoint {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 4444;

    private static final int PANEL_COLUMN = 137;
    private static final int SLOT_COUNT = 10;

    private static final int PREVIOUS = -1;
    private static final int NEXT = 1;

    private static final int MOVE_UP_KEY = 'w';
    private static final int MOVE_LEFT_KEY = 'a';
    private static final int MOVE_DOWN_KEY = 's';
    private static final int MOVE_RIGHT_KEY = 'd';
    private static final int USE_KEY = 'e';
    private static final int PICK_UP_KEY = 'f';
    private static final int GIVE_KEY = 'g';
    private static final int DROP_KEY = 'r';
    private static final int CANCEL_KEY = 'q';
    private static final int ZERO_KEY = '0';

    private static final String LOGGING_CONFIG = "/client-logging.properties";
    private static final Path LOG_DIRECTORY = Path.of("logs");
    private static final String CONTACT_ADMIN = "contact the administrator with the logs in ";

    private static final Logger LOGGER = Logger.getLogger(ClientEntryPoint.class.getName());

    static {
        try (InputStream config = ClientEntryPoint.class.getResourceAsStream(LOGGING_CONFIG)) {
            Files.createDirectories(LOG_DIRECTORY);

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
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            LOGGER.log(Level.CONFIG, "Connected to {0}:{1}.", new Object[] {SERVER_HOST, SERVER_PORT});
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

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8))) {

            ClientState state = new ClientState();
            Selection selection = new Selection(state);
            GameRenderer renderer = buildRenderer(console, state, selection);
            RequestSender sender = new RequestSender(writer);

            InputLoop loop = new InputLoop(new KeyReader(console), state, selection,
                buildBindings(state, selection, renderer), sender);

            new PlayerClient(console, reader, renderer, loop).run();
        }
    }

    private GameRenderer buildRenderer(Console console, ClientState state, Selection selection) {
        ItemFormatter items = new ItemFormatter();

        return new GameRendererImpl(console, state, selection,
            new MapRenderer(console, items),
            new StatsRenderer(console, PANEL_COLUMN),
            new BackpackRenderer(console, items, PANEL_COLUMN),
            new MessageRenderer(console, PANEL_COLUMN),
            new SelectionRenderer(console, selection, items, PANEL_COLUMN));
    }

    private KeyBindings buildBindings(ClientState state, Selection selection, GameRenderer renderer) {
        KeyBindings bindings = new KeyBindings();

        bindEveryMode(bindings, state);
        bindExploring(bindings, selection, renderer);

        bindSelecting(bindings, selection, renderer, Mode.CHOOSING_TARGET, UseRequest::new);
        bindSelecting(bindings, selection, renderer, Mode.CHOOSING_TREASURE, PickUpRequest::new);
        bindSelecting(bindings, selection, renderer, Mode.CHOOSING_PLAYER, GiveRequest::new);

        return bindings;
    }

    private void bindEveryMode(KeyBindings bindings, ClientState state) {
        bindEverywhere(bindings, KeyReader.ESCAPE, new QuitCommand(state));

        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            bindEverywhere(bindings, slotKey(slot), new SelectSlotCommand(slot));
        }
    }

    private void bindEverywhere(KeyBindings bindings, int key, ClientCommand command) {
        for (Mode mode : Mode.values()) {
            bindings.bind(mode, key, command);
        }
    }

    private int slotKey(int slot) {
        return slot == SLOT_COUNT - 1 ? ZERO_KEY : ZERO_KEY + slot + 1;
    }

    private void bindExploring(KeyBindings bindings, Selection selection, GameRenderer renderer) {
        bindMove(bindings, MOVE_UP_KEY, KeyReader.ARROW_UP, Direction.UP);
        bindMove(bindings, MOVE_DOWN_KEY, KeyReader.ARROW_DOWN, Direction.DOWN);
        bindMove(bindings, MOVE_LEFT_KEY, KeyReader.ARROW_LEFT, Direction.LEFT);
        bindMove(bindings, MOVE_RIGHT_KEY, KeyReader.ARROW_RIGHT, Direction.RIGHT);

        bindings.bind(Mode.EXPLORING, USE_KEY, new UseCommand(selection, renderer));
        bindings.bind(Mode.EXPLORING, PICK_UP_KEY,
            new OpenListCommand(selection, renderer, Mode.CHOOSING_TREASURE));
        bindings.bind(Mode.EXPLORING, GIVE_KEY,
            new OpenListCommand(selection, renderer, Mode.CHOOSING_PLAYER));
        bindings.bind(Mode.EXPLORING, DROP_KEY, new DropCommand());
    }

    private void bindMove(KeyBindings bindings, int letterKey, int arrowKey, Direction direction) {
        MoveCommand move = new MoveCommand(direction);

        bindings.bind(Mode.EXPLORING, letterKey, move);
        bindings.bind(Mode.EXPLORING, arrowKey, move);
    }

    private void bindSelecting(KeyBindings bindings, Selection selection, GameRenderer renderer, Mode mode,
                               IntFunction<Request> requestFor) {
        bindings.bind(mode, KeyReader.ARROW_UP, new HighlightCommand(selection, renderer, PREVIOUS));
        bindings.bind(mode, KeyReader.ARROW_DOWN, new HighlightCommand(selection, renderer, NEXT));
        bindings.bind(mode, KeyReader.ENTER, new ConfirmCommand(selection, renderer, requestFor));
        bindings.bind(mode, CANCEL_KEY, new CancelCommand(selection, renderer));
    }

    private String contactAdmin() {
        return CONTACT_ADMIN + LOG_DIRECTORY.toAbsolutePath() + ".";
    }

}
