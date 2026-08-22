package bg.sofia.uni.fmi.mjt.dungeonsonline.client.input;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.RequestSender;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.DropRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.GiveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.PickUpRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.QuitRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.SelectRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.UseRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputLoop {

    private static final int ESCAPE = 27;
    private static final int BRACKET = 91;
    private static final int SS3 = 79;
    private static final int ARROW_UP = 65;
    private static final int ARROW_DOWN = 66;
    private static final int ARROW_RIGHT = 67;
    private static final int ARROW_LEFT = 68;
    private static final int ENTER = 13;
    private static final int NEWLINE = 10;
    private static final int NOTHING = -2;

    private static final long ESCAPE_TIMEOUT = 200;

    private static final int LAST_SLOT_KEY = '0';
    private static final int SLOT_COUNT = 10;

    private final Console console;
    private final ClientState state;
    private final GameRenderer renderer;
    private final RequestSender sender;

    private boolean playing = true;

    public InputLoop(Console console, ClientState state, GameRenderer renderer, RequestSender sender) {
        this.console = console;
        this.state = state;
        this.renderer = renderer;
        this.sender = sender;
    }

    public void run() throws IOException {
        console.enterRawMode();

        while (playing) {
            int key = console.read();
            if (key < 0) {
                break;
            }

            if (key == ESCAPE) {
                handleEscape();
            } else {
                handleKey(key);
            }
        }
    }

    private void handleEscape() throws IOException {
        int next = console.read(ESCAPE_TIMEOUT);

        if (next == NOTHING) {
            quit();
            return;
        }

        if (next != BRACKET && next != SS3) {
            return;
        }

        int arrow = console.read(ESCAPE_TIMEOUT);
        switch (arrow) {
            case ARROW_UP -> up();
            case ARROW_DOWN -> down();
            case ARROW_LEFT -> move(Direction.LEFT);
            case ARROW_RIGHT -> move(Direction.RIGHT);
            default -> { }
        }
    }

    private void handleKey(int key) {
        if (key == ENTER || key == NEWLINE) {
            confirm();
            return;
        }

        if (key >= '0' && key <= '9') {
            sender.send(new SelectRequest(slotOf(key)));
            return;
        }

        switch (Character.toLowerCase(key)) {
            case 'w' -> move(Direction.UP);
            case 's' -> move(Direction.DOWN);
            case 'a' -> move(Direction.LEFT);
            case 'd' -> move(Direction.RIGHT);
            case 'e' -> use();
            case 'f' -> open(Mode.CHOOSING_TREASURE);
            case 'g' -> open(Mode.CHOOSING_PLAYER);
            case 'r' -> sender.send(new DropRequest());
            case 'q' -> cancel();
            default -> { }
        }
    }

    private void move(Direction direction) {
        if (state.getMode() != Mode.EXPLORING) {
            return;
        }

        sender.send(new MoveRequest(direction));
    }

    private void use() {
        if (state.getMode() != Mode.EXPLORING) {
            return;
        }

        if (state.actorsOnMyTile().isEmpty()) {
            sender.send(new UseRequest(null));
            return;
        }

        open(Mode.CHOOSING_TARGET);
    }

    private void open(Mode mode) {
        if (state.getMode() != Mode.EXPLORING) {
            return;
        }

        List<Integer> ids = candidateIds(mode);
        if (ids.isEmpty()) {
            renderer.renderError("There is nothing to choose here!");
            return;
        }

        state.setMode(mode);
        state.setHighlightedId(ids.getFirst());
        renderer.renderSelection();
    }

    private void confirm() {
        Mode mode = state.getMode();
        Integer chosen = state.getHighlightedId();

        if (mode == Mode.EXPLORING || chosen == null) {
            return;
        }

        close();

        switch (mode) {
            case CHOOSING_TARGET -> sender.send(new UseRequest(chosen));
            case CHOOSING_TREASURE -> sender.send(new PickUpRequest(chosen));
            case CHOOSING_PLAYER -> sender.send(new GiveRequest(chosen));
            default -> { }
        }
    }

    private void cancel() {
        if (state.getMode() == Mode.EXPLORING) {
            return;
        }

        close();
    }

    private void close() {
        state.setMode(Mode.EXPLORING);
        state.setHighlightedId(null);
        renderer.renderSelection();
    }

    private void quit() {
        sender.send(new QuitRequest());
        playing = false;
    }

    private void up() {
        step(-1);
    }

    private void down() {
        step(1);
    }

    private void step(int offset) {
        if (state.getMode() == Mode.EXPLORING) {
            move(offset < 0 ? Direction.UP : Direction.DOWN);
            return;
        }

        List<Integer> ids = candidateIds(state.getMode());
        if (ids.isEmpty()) {
            close();
            return;
        }

        int current = Math.max(0, ids.indexOf(state.getHighlightedId()));
        int next = Math.floorMod(current + offset, ids.size());

        state.setHighlightedId(ids.get(next));
        renderer.renderSelection();
    }

    private List<Integer> candidateIds(Mode mode) {
        List<Integer> ids = new ArrayList<>();

        if (mode == Mode.CHOOSING_TREASURE) {
            for (TreasureDTO treasure : state.treasuresOnMyTile()) {
                ids.add(treasure.id());
            }

            return ids;
        }

        List<ActorDTO> actors = mode == Mode.CHOOSING_PLAYER
            ? state.playersOnMyTile()
            : state.actorsOnMyTile();

        for (ActorDTO actor : actors) {
            ids.add(actor.id());
        }

        return ids;
    }

    private int slotOf(int key) {
        return key == LAST_SLOT_KEY ? SLOT_COUNT - 1 : key - '1';
    }

}
