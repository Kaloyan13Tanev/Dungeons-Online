package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpenListCommandTest {

    private static final Mode MODE = Mode.CHOOSING_TREASURE;

    @Mock
    private Selection selection;
    @Mock
    private GameRenderer renderer;

    private OpenListCommand command;

    @BeforeEach
    void setUp() {
        command = new OpenListCommand(selection, renderer, MODE);
    }

    @Test
    void testExecuteShowsTheSelectionItOpened() {
        when(selection.open(MODE)).thenReturn(true);

        command.execute();

        verify(renderer).renderSelection();
    }

    @Test
    void testExecuteShowsAnErrorWhenThereIsNothingToChoose() {
        when(selection.open(MODE)).thenReturn(false);

        command.execute();

        verify(renderer).renderError(anyString());
    }

    @Test
    void testExecuteMakesNoRequest() {
        when(selection.open(MODE)).thenReturn(true);

        assertTrue(command.execute().isEmpty(),
            "OpenListCommand should ask the server for nothing before the player has chosen");
    }

}
