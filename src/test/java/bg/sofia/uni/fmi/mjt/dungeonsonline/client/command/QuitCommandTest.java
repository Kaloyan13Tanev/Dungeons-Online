package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.QuitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class QuitCommandTest {

    @Mock
    private ClientState state;

    private QuitCommand command;

    @BeforeEach
    void setUp() {
        command = new QuitCommand(state);
    }

    @Test
    void testExecuteStopsThePlayerFromPlaying() {
        command.execute();

        verify(state).stopPlaying();
    }

    @Test
    void testExecuteMakesAQuitRequest() {
        assertEquals(Optional.of(new QuitRequest()), command.execute(),
            "QuitCommand should make a quit request so that the server lets the player go");
    }

}
