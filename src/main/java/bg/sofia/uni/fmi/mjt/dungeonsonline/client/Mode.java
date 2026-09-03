package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import java.util.List;
import java.util.function.Function;

public enum Mode {

    EXPLORING(state -> List.of()),
    CHOOSING_TARGET(ClientState::targetIds),
    CHOOSING_TREASURE(ClientState::treasureIds),
    CHOOSING_PLAYER(ClientState::playerIds);

    private final Function<ClientState, List<Integer>> candidates;

    Mode(Function<ClientState, List<Integer>> candidates) {
        this.candidates = candidates;
    }

    public List<Integer> candidateIds(ClientState state) {
        return candidates.apply(state);
    }

}
