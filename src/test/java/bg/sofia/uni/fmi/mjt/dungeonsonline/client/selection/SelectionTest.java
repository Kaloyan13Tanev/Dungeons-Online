package bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SelectionTest {

    private static final int FIRST_ID = 1;
    private static final int SECOND_ID = 2;

    private static final List<Integer> IDS = List.of(FIRST_ID, SECOND_ID);

    private static final int FORWARD = 1;
    private static final int BACKWARD = -1;

    @Mock
    private ClientState state;

    private Selection selection;

    @BeforeEach
    void setUp() {
        selection = new Selection(state);
    }

    @Test
    void testOpenEntersTheModeAndChoosesItsFirstCandidate() {
        when(state.targetIds()).thenReturn(IDS);

        assertTrue(selection.open(Mode.CHOOSING_TARGET),
            "Selection should report that it opened a mode that has something to choose from");
        assertEquals(Mode.CHOOSING_TARGET, selection.mode(),
            "Selection should enter the mode it was opened with");
        assertEquals(FIRST_ID, selection.chosen(),
            "Selection should highlight the first candidate of the mode it opened");
    }

    @Test
    void testOpenKeepsTheCurrentModeWhenThereIsNothingToChoose() {
        when(state.targetIds()).thenReturn(List.of());

        assertFalse(selection.open(Mode.CHOOSING_TARGET),
            "Selection should report that a mode with nothing to choose from did not open");
        assertEquals(Mode.EXPLORING, selection.mode(),
            "Selection should stay in the mode it was in when there is nothing to choose");
    }

    @Test
    void testCloseLeavesTheModeAndDropsTheHighlight() {
        when(state.targetIds()).thenReturn(IDS);
        selection.open(Mode.CHOOSING_TARGET);

        selection.close();

        assertEquals(Mode.EXPLORING, selection.mode(),
            "Selection should send the player back to exploring when it closes");
        assertNull(selection.chosen(),
            "Selection should highlight nothing once it has closed");
    }

    @Test
    void testStepHighlightsTheCandidateAtTheOffset() {
        when(state.targetIds()).thenReturn(IDS);
        selection.open(Mode.CHOOSING_TARGET);

        selection.step(FORWARD);

        assertEquals(SECOND_ID, selection.chosen(),
            "Selection should move the highlight by the offset it was stepped with");
    }

    @Test
    void testStepWrapsAroundToTheLastCandidate() {
        when(state.targetIds()).thenReturn(IDS);
        selection.open(Mode.CHOOSING_TARGET);

        selection.step(BACKWARD);

        assertEquals(SECOND_ID, selection.chosen(),
            "Selection should wrap around to the last candidate when stepped back from the first");
    }

    @Test
    void testStepClosesTheSelectionWhenNothingIsLeftToChoose() {
        when(state.targetIds()).thenReturn(IDS, List.of());
        selection.open(Mode.CHOOSING_TARGET);

        selection.step(FORWARD);

        assertEquals(Mode.EXPLORING, selection.mode(),
            "Selection should close when the player steps and the candidates are gone");
    }

    @Test
    void testReviseKeepsTheHighlightWhenItIsStillACandidate() {
        when(state.targetIds()).thenReturn(IDS);
        selection.open(Mode.CHOOSING_TARGET);
        selection.step(FORWARD);

        selection.revise();

        assertEquals(SECOND_ID, selection.chosen(),
            "Selection should leave a highlight that the new candidates still hold alone");
    }

    @Test
    void testReviseHighlightsTheFirstCandidateWhenTheHighlightedOneIsGone() {
        when(state.targetIds()).thenReturn(IDS, List.of(SECOND_ID));
        selection.open(Mode.CHOOSING_TARGET);

        selection.revise();

        assertEquals(SECOND_ID, selection.chosen(),
            "Selection should highlight the first candidate when the highlighted one is gone");
    }

    @Test
    void testReviseClosesTheSelectionWhenNothingIsLeftToChoose() {
        when(state.targetIds()).thenReturn(IDS, List.of());
        selection.open(Mode.CHOOSING_TARGET);

        selection.revise();

        assertEquals(Mode.EXPLORING, selection.mode(),
            "Selection should close when the state leaves the player with nothing to choose");
    }

}
