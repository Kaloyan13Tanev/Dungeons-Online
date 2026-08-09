package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class ClientState {

    private static final int MESSAGE_HISTORY = 6;

    private int playerId;
    private TerrainDTO terrain;
    private GameStateDTO state;
    private final Deque<Message> messages = new ArrayDeque<>();

    private Mode mode = Mode.EXPLORING;
    private Integer highlightedId;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public TerrainDTO getTerrain() {
        return terrain;
    }

    public void setTerrain(TerrainDTO terrain) {
        this.terrain = terrain;
    }

    public GameStateDTO getState() {
        return state;
    }

    public void setState(GameStateDTO state) {
        this.state = state;
    }

    public List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public void addMessage(String text) {
        add(new Message(text, false));
    }

    public void addError(String text) {
        add(new Message(text, true));
    }

    private void add(Message message) {
        messages.addLast(message);

        while (messages.size() > MESSAGE_HISTORY) {
            messages.removeFirst();
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Integer getHighlightedId() {
        return highlightedId;
    }

    public void setHighlightedId(Integer highlightedId) {
        this.highlightedId = highlightedId;
    }

    public Optional<ActorDTO> getSelf() {
        if (state == null) {
            return Optional.empty();
        }

        return state.actors().stream()
            .filter(actor -> actor.id() == playerId)
            .findFirst();
    }

    public List<ActorDTO> actorsOnMyTile() {
        Optional<ActorDTO> self = getSelf();
        if (self.isEmpty()) {
            return List.of();
        }

        List<ActorDTO> onTile = new ArrayList<>();
        for (ActorDTO actor : state.actors()) {
            if (actor.id() != playerId && sameTile(actor.row(), actor.col(), self.get())) {
                onTile.add(actor);
            }
        }

        return List.copyOf(onTile);
    }

    public List<ActorDTO> playersOnMyTile() {
        List<ActorDTO> onTile = new ArrayList<>();

        for (ActorDTO actor : actorsOnMyTile()) {
            if (actor.kind() == ActorKind.PLAYER) {
                onTile.add(actor);
            }
        }

        return List.copyOf(onTile);
    }

    public List<TreasureDTO> treasuresOnMyTile() {
        Optional<ActorDTO> self = getSelf();
        if (self.isEmpty()) {
            return List.of();
        }

        List<TreasureDTO> onTile = new ArrayList<>();
        for (TreasureDTO treasure : state.treasures()) {
            if (sameTile(treasure.row(), treasure.col(), self.get())) {
                onTile.add(treasure);
            }
        }

        return List.copyOf(onTile);
    }

    private boolean sameTile(int row, int col, ActorDTO self) {
        return row == self.row() && col == self.col();
    }

}
