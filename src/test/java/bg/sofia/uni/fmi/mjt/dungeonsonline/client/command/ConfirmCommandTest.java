package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.PickUpRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfirmCommandTest {

    private static final int CHOSEN_ID = 7;

    private static final Request REQUEST = new PickUpRequest(CHOSEN_ID);

    @Mock
    private Selection selection;
    @Mock
    private GameRenderer renderer;
    @Mock
    private IntFunction<Request> requestFor;

    private ConfirmCommand command;

    @BeforeEach
    void setUp() {
        command = new ConfirmCommand(selection, renderer, requestFor);
    }

    @Test
    void testExecuteMakesTheRequestItsFunctionMadeOfTheChosenId() {
        when(selection.chosen()).thenReturn(CHOSEN_ID);
        when(requestFor.apply(CHOSEN_ID)).thenReturn(REQUEST);

        assertEquals(Optional.of(REQUEST), command.execute(),
            "ConfirmCommand should ask the server for what the player chose");
    }

    @Test
    void testExecuteClosesTheSelectionAndShowsIt() {
        when(selection.chosen()).thenReturn(CHOSEN_ID);
        when(requestFor.apply(CHOSEN_ID)).thenReturn(REQUEST);

        command.execute();

        verify(selection).close();
        verify(renderer).renderSelection();
    }

    @Test
    void testExecuteMakesNoRequestWhenNothingIsChosen() {
        when(selection.chosen()).thenReturn(null);

        assertTrue(command.execute().isEmpty(),
            "ConfirmCommand should ask the server for nothing while the player has chosen nothing");
    }

}
