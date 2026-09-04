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
public class CancelCommandTest {

    @Mock
    private Selection selection;
    @Mock
    private GameRenderer renderer;

    private CancelCommand command;

    @BeforeEach
    void setUp() {
        command = new CancelCommand(selection, renderer);
    }

    @Test
    void testExecuteClosesTheSelectionAndShowsIt() {
        command.execute();

        verify(selection).close();
        verify(renderer).renderSelection();
    }

    @Test
    void testExecuteMakesNoRequest() {
        assertTrue(command.execute().isEmpty(),
            "CancelCommand should ask the server for nothing when the player calls the selection off");
    }

}
