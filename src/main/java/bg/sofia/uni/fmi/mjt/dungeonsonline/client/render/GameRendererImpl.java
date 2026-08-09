package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.HandshakeResponse;

import java.util.ArrayList;
import java.util.List;

public class GameRendererImpl implements GameRenderer {

    private final Console console;
    private final ClientState state;

    private final MapRenderer map;
    private final StatsRenderer stats;
    private final BackpackRenderer backpack;
    private final MessageRenderer messages;
    private final SelectionRenderer selection;

    public GameRendererImpl(Console console, ClientState state, MapRenderer map, StatsRenderer stats,
                            BackpackRenderer backpack, MessageRenderer messages,
                            SelectionRenderer selection) {
        this.console = console;
        this.state = state;
        this.map = map;
        this.stats = stats;
        this.backpack = backpack;
        this.messages = messages;
        this.selection = selection;
    }

    @Override
    public synchronized void renderHandshake(HandshakeResponse response) {
        state.setPlayerId(response.playerId());
        state.setTerrain(response.terrain());

        console.clearScreen();
        map.render(state);
        messages.render(state);
    }

    @Override
    public synchronized void renderState(GameStateDTO newState) {
        state.setState(newState);
        reviseSelection();

        map.render(state);
        stats.render(state);
        backpack.render(state);
        selection.render(state);
    }

    @Override
    public synchronized void renderEvent(String message) {
        state.addMessage(message);
        messages.render(state);
    }

    @Override
    public synchronized void renderError(String message) {
        state.addError(message);
        messages.render(state);
    }

    @Override
    public synchronized void renderSelection() {
        selection.render(state);
    }

    private void reviseSelection() {
        if (state.getMode() == Mode.EXPLORING) {
            return;
        }

        List<Integer> ids = candidateIds();
        if (ids.isEmpty()) {
            state.setMode(Mode.EXPLORING);
            state.setHighlightedId(null);
            return;
        }

        if (!ids.contains(state.getHighlightedId())) {
            state.setHighlightedId(ids.get(0));
        }
    }

    private List<Integer> candidateIds() {
        List<Integer> ids = new ArrayList<>();

        if (state.getMode() == Mode.CHOOSING_TREASURE) {
            for (TreasureDTO treasure : state.treasuresOnMyTile()) {
                ids.add(treasure.id());
            }

            return ids;
        }

        List<ActorDTO> actors = state.getMode() == Mode.CHOOSING_PLAYER
            ? state.playersOnMyTile()
            : state.actorsOnMyTile();

        for (ActorDTO actor : actors) {
            ids.add(actor.id());
        }

        return ids;
    }

}
