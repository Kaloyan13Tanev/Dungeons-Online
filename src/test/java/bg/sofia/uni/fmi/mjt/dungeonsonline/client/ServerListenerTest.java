package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.InvalidResponseException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ResponseMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.TerrainKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.ErrorResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.EventResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.HandshakeResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.Response;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.StateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServerListenerTest {

    private static final int PLAYER_ID = 1;

    private static final String LINE = "serialized response";

    private static final String MESSAGE = "A minion died.";
    private static final String ERROR = "You have nothing to drop!";
    private static final String REASON = "Server is full. Try again later.";
    private static final String LOST_CONNECTION = "Lost the connection to the server.";

    private static final TerrainDTO TERRAIN = new TerrainDTO(List.of(List.of(TerrainKind.GROUND)));
    private static final GameStateDTO STATE = new GameStateDTO(List.of(), List.of(), null);

    private static final HandshakeResponse ACCEPTED =
        new HandshakeResponse(true, PLAYER_ID, null, TERRAIN);
    private static final HandshakeResponse REFUSED =
        new HandshakeResponse(false, PLAYER_ID, REASON, null);

    @Mock
    private BufferedReader reader;
    @Mock
    private GameRenderer renderer;
    @Mock
    private ResponseMapper mapper;

    private ServerListener listener;

    @BeforeEach
    void setUp() {
        listener = new ServerListener(reader, renderer, mapper);
    }

    @Test
    void testRunDeserializesResponse() throws IOException {
        mockResponse(new EventResponse(MESSAGE));

        listener.run();

        verify(mapper).deserialize(LINE);
    }
    
    @Test
    void testRunRendersTheStateOfAStateResponse() throws IOException {
        mockResponse(new StateResponse(STATE));

        listener.run();

        verify(renderer).renderState(STATE);
    }

    @Test
    void testRunRendersTheMessageOfAnEventResponse() throws IOException {
        mockResponse(new EventResponse(MESSAGE));

        listener.run();

        verify(renderer).renderEvent(MESSAGE);
    }

    @Test
    void testRunRendersTheMessageOfAnErrorResponse() throws IOException {
        mockResponse(new ErrorResponse(ERROR));

        listener.run();

        verify(renderer).renderError(ERROR);
    }

    @Test
    void testRunRendersAHandshakeTheServerAccepted() throws IOException {
        mockResponse(ACCEPTED);

        listener.run();

        verify(renderer).renderHandshake(ACCEPTED);
    }

    @Test
    void testRunRendersTheReasonOfAHandshakeTheServerRefused() throws IOException {
        mockResponse(REFUSED);

        listener.run();

        verify(renderer).renderError(REASON);
    }

    @Test
    void testRunStopsListeningWhenTheServerRefusesTheHandshake() throws IOException {
        mockResponse(REFUSED);

        listener.run();

        assertFalse(listener.isRunning(),
            "ServerListener should stop listening once the server refuses the handshake");
    }

    @Test
    void testRunSkipsAResponseItCannotRead() throws IOException {
        when(reader.readLine()).thenReturn(LINE, null);
        when(mapper.deserialize(LINE)).thenThrow(new InvalidResponseException("Unreadable"));

        listener.run();

        verifyNoInteractions(renderer);
    }

    @Test
    void testRunTellsThePlayerWhenTheConnectionIsLost() throws IOException {
        when(reader.readLine()).thenThrow(new IOException("Broken pipe"));

        listener.run();

        verify(renderer).renderError(LOST_CONNECTION);
    }

    @Test
    void testRunStopsListeningWhenTheServerStopsSending() throws IOException {
        when(reader.readLine()).thenReturn(null);

        listener.run();

        assertFalse(listener.isRunning(),
            "ServerListener should stop listening once the server sends nothing more");
    }

    private void mockResponse(Response response) throws IOException {
        when(reader.readLine()).thenReturn(LINE, (String) null);
        when(mapper.deserialize(LINE)).thenReturn(response);
    }

}
