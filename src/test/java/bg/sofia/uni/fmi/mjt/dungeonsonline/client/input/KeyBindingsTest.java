package bg.sofia.uni.fmi.mjt.dungeonsonline.client.input;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.ClientCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.DropRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KeyBindingsTest {

    private static final int KEY = 'r';
    private static final int UNBOUND_KEY = 'x';

    private static final Request REQUEST = new DropRequest();

    @Mock
    private ClientCommand command;

    private KeyBindings bindings;

    @BeforeEach
    void setUp() {
        bindings = new KeyBindings();
    }

    @Test
    void testPressReturnsWhatTheCommandBoundToTheKeyMade() {
        when(command.execute()).thenReturn(Optional.of(REQUEST));
        bindings.bind(Mode.EXPLORING, KEY, command);

        assertEquals(Optional.of(REQUEST), bindings.press(Mode.EXPLORING, KEY),
            "KeyBindings should return the request the command of the pressed key made");
    }

    @Test
    void testPressReturnsNothingWhenTheKeyIsBoundToNoCommand() {
        bindings.bind(Mode.EXPLORING, KEY, command);

        assertTrue(bindings.press(Mode.EXPLORING, UNBOUND_KEY).isEmpty(),
            "KeyBindings should return nothing for a key that is bound to no command");
    }

    @Test
    void testPressReturnsNothingWhenTheKeyIsBoundInAnotherModeOnly() {
        bindings.bind(Mode.EXPLORING, KEY, command);

        assertTrue(bindings.press(Mode.CHOOSING_TARGET, KEY).isEmpty(),
            "KeyBindings should keep the keys bound in one mode out of the other modes");
    }

}
