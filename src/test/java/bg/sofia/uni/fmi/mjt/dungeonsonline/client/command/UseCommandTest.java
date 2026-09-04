package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.UseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UseCommandTest {

    @Mock
    private Selection selection;
    @Mock
    private GameRenderer renderer;

    private UseCommand command;

    @BeforeEach
    void setUp() {
        command = new UseCommand(selection, renderer);
    }

    @Test
    void testExecuteMakesAUseRequestWithoutATargetWhenThereIsNobodyToChoose() {
        when(selection.open(Mode.CHOOSING_TARGET)).thenReturn(false);

        assertEquals(Optional.of(new UseRequest(null)), command.execute(),
            "UseCommand should use the item on the player themselves when the tile holds nobody else");
    }

    @Test
    void testExecuteShowsTheTargetSelectionItOpened() {
        when(selection.open(Mode.CHOOSING_TARGET)).thenReturn(true);

        command.execute();

        verify(renderer).renderSelection();
    }

    @Test
    void testExecuteMakesNoRequestWhileTheTargetIsBeingChosen() {
        when(selection.open(Mode.CHOOSING_TARGET)).thenReturn(true);

        assertTrue(command.execute().isEmpty(),
            "UseCommand should wait for the player to choose a target before it asks the server");
    }

}
