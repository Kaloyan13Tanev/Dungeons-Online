package bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngine;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEvent;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.EmptySlotException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.RequestMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.ErrorResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.EventResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.StateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestHandlerTest {

    private static final int FIRST_PLAYER_ID = 1;
    private static final int SECOND_PLAYER_ID = 2;

    private static final String REQUEST = "serialized request";

    private static final String UNREADABLE_REQUEST = "That command could not be read by the server.";
    private static final String FAILED_REQUEST = "Something went wrong while handling that command.";
    private static final String REFUSAL = "You have nothing to drop!";

    private static final String FIRST_MESSAGE = "Player 1 joined the game.";
    private static final String SECOND_MESSAGE = "Player 2 left the game.";

    private static final MoveRequest MOVE = new MoveRequest(Direction.UP);

    private static final GameStateDTO FIRST_STATE = new GameStateDTO(
        List.of(new ActorDTO(FIRST_PLAYER_ID, ActorKind.PLAYER, 0, 0)), List.of(), null);
    private static final GameStateDTO SECOND_STATE = new GameStateDTO(
        List.of(new ActorDTO(SECOND_PLAYER_ID, ActorKind.PLAYER, 0, 0)), List.of(), null);

    @Mock
    private ConnectionRegistry registry;
    @Mock
    private GameEngine engine;
    @Mock
    private Router router;
    @Mock
    private RequestMapper mapper;

    private RequestHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RequestHandler(registry, engine, router, mapper);
    }

    @Test
    void testHandleRoutesTheRequestItRead() {
        when(mapper.deserialize(REQUEST)).thenReturn(MOVE);

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(router).route(FIRST_PLAYER_ID, MOVE);
    }

    @Test
    void testHandleTellsThePlayerWhenTheirRequestCannotBeRead() {
        when(mapper.deserialize(REQUEST)).thenThrow(new InvalidRequestException("Unreadable"));

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(registry).sendTo(FIRST_PLAYER_ID, new ErrorResponse(UNREADABLE_REQUEST));
    }

    @Test
    void testHandleTellsThePlayerWhyTheirActionWasRefused() {
        when(mapper.deserialize(REQUEST)).thenReturn(MOVE);
        when(router.route(FIRST_PLAYER_ID, MOVE)).thenThrow(new EmptySlotException(REFUSAL));

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(registry).sendTo(FIRST_PLAYER_ID, new ErrorResponse(REFUSAL));
    }

    @Test
    void testHandleTellsThePlayerWhenTheRequestCouldNotBeHandled() {
        when(mapper.deserialize(REQUEST)).thenReturn(MOVE);
        when(router.route(FIRST_PLAYER_ID, MOVE)).thenThrow(new IllegalStateException("Broken"));

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(registry).sendTo(FIRST_PLAYER_ID, new ErrorResponse(FAILED_REQUEST));
    }

    @Test
    void testDistributeSendsEveryEventToItsOwnRecipients() {
        GameEvent joined = new GameEvent(Set.of(FIRST_PLAYER_ID), FIRST_MESSAGE);
        GameEvent left = new GameEvent(Set.of(SECOND_PLAYER_ID), SECOND_MESSAGE);

        handler.distribute(List.of(joined, left));

        verify(registry).sendTo(Set.of(FIRST_PLAYER_ID), new EventResponse(FIRST_MESSAGE));
        verify(registry).sendTo(Set.of(SECOND_PLAYER_ID), new EventResponse(SECOND_MESSAGE));
    }

    @Test
    void testDistributeSendsEveryPlayerTheirOwnState() {
        when(engine.stateForAll()).thenReturn(Map.of(FIRST_PLAYER_ID, FIRST_STATE,
            SECOND_PLAYER_ID, SECOND_STATE));

        handler.distribute(List.of());

        verify(registry).sendTo(FIRST_PLAYER_ID, new StateResponse(FIRST_STATE));
        verify(registry).sendTo(SECOND_PLAYER_ID, new StateResponse(SECOND_STATE));
    }

}
