package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModeTest {

    private static final List<Integer> CANDIDATE_IDS = List.of(1, 2);

    @Mock
    private ClientState state;

    @Test
    void testExploringOffersNoCandidates() {
        assertTrue(Mode.EXPLORING.candidateIds(state).isEmpty(),
            "Mode EXPLORING should offer nothing to choose from");
    }

    @Test
    void testChoosingTargetOffersTheTargetsOfThePlayer() {
        when(state.targetIds()).thenReturn(CANDIDATE_IDS);

        assertEquals(CANDIDATE_IDS, Mode.CHOOSING_TARGET.candidateIds(state),
            "Mode CHOOSING_TARGET should offer the targets the state of the client knows");
    }

    @Test
    void testChoosingTreasureOffersTheTreasuresOnTheTile() {
        when(state.treasureIds()).thenReturn(CANDIDATE_IDS);

        assertEquals(CANDIDATE_IDS, Mode.CHOOSING_TREASURE.candidateIds(state),
            "Mode CHOOSING_TREASURE should offer the treasures the state of the client knows");
    }

    @Test
    void testChoosingPlayerOffersThePlayersOnTheTile() {
        when(state.playerIds()).thenReturn(CANDIDATE_IDS);

        assertEquals(CANDIDATE_IDS, Mode.CHOOSING_PLAYER.candidateIds(state),
            "Mode CHOOSING_PLAYER should offer the players the state of the client knows");
    }

}
