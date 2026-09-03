package bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;

import java.util.List;

public class Selection {

    private final ClientState state;

    private Mode mode = Mode.EXPLORING;
    private Integer highlightedId;

    public Selection(ClientState state) {
        this.state = state;
    }

    public synchronized Mode mode() {
        return mode;
    }

    public synchronized Integer chosen() {
        return highlightedId;
    }

    public synchronized boolean open(Mode mode) {
        List<Integer> ids = mode.candidateIds(state);
        if (ids.isEmpty()) {
            return false;
        }

        this.mode = mode;
        this.highlightedId = ids.getFirst();

        return true;
    }

    public synchronized void close() {
        mode = Mode.EXPLORING;
        highlightedId = null;
    }

    public synchronized void step(int offset) {
        List<Integer> ids = mode.candidateIds(state);
        if (ids.isEmpty()) {
            close();
            return;
        }

        int current = Math.max(0, ids.indexOf(highlightedId));
        highlightedId = ids.get(Math.floorMod(current + offset, ids.size()));
    }

    public synchronized void revise() {
        if (mode == Mode.EXPLORING) {
            return;
        }

        List<Integer> ids = mode.candidateIds(state);
        if (ids.isEmpty()) {
            close();
            return;
        }

        if (!ids.contains(highlightedId)) {
            highlightedId = ids.getFirst();
        }
    }

}
