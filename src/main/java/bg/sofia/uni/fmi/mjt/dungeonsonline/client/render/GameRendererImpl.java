package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.HandshakeResponse;

public class GameRendererImpl implements GameRenderer {

    private final Console console;
    private final ClientState state;
    private final Selection selection;

    private final MapRenderer map;
    private final StatsRenderer stats;
    private final BackpackRenderer backpack;
    private final MessageRenderer messages;
    private final SelectionRenderer selectionRenderer;

    public GameRendererImpl(Console console, ClientState state, Selection selection, MapRenderer map,
                            StatsRenderer stats, BackpackRenderer backpack, MessageRenderer messages,
                            SelectionRenderer selectionRenderer) {
        this.console = console;
        this.state = state;
        this.selection = selection;
        this.map = map;
        this.stats = stats;
        this.backpack = backpack;
        this.messages = messages;
        this.selectionRenderer = selectionRenderer;
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
        selection.revise();

        map.render(state);
        stats.render(state);
        backpack.render(state);
        selectionRenderer.render(state);
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
        selectionRenderer.render(state);
    }

}
