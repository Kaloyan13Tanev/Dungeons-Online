package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChangeSelectionCommandTest {

    private static final int OFFSET = -1;

    @Mock
    private Selection selection;
    @Mock
    private GameRenderer renderer;

    private ChangeSelectionCommand command;

    @BeforeEach
    void setUp() {
        command = new ChangeSelectionCommand(selection, renderer, OFFSET);
    }

    @Test
    void testExecuteStepsTheSelectionByItsOffsetAndShowsIt() {
        command.execute();

        verify(selection).step(OFFSET);
        verify(renderer).renderSelection();
    }

    @Test
    void testExecuteMakesNoRequest() {
        assertTrue(command.execute().isEmpty(),
            "ChangeSelectionCommand should ask the server for nothing when the player moves the highlight");
    }

}
