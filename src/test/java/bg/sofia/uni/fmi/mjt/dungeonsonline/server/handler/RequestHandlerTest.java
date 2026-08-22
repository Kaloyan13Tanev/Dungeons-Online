package bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngine;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEvent;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.EmptySlotException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.RequestMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.DropRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.GiveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.PickUpRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.QuitRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.SelectRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.UseRequest;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestHandlerTest {

    private static final int FIRST_PLAYER_ID = 1;
    private static final int SECOND_PLAYER_ID = 2;

    private static final int TREASURE_ID = 1;
    private static final int TARGET_ID = 10;
    private static final int SLOT = 1;

    private static final String REQUEST = "serialized request";

    private static final String UNREADABLE_REQUEST = "That command could not be read by the server.";
    private static final String FAILED_REQUEST = "Something went wrong while handling that command.";
    private static final String REFUSAL = "You have nothing to drop!";

    private static final String FIRST_MESSAGE = "Player 1 joined the game.";
    private static final String SECOND_MESSAGE = "Player 2 left the game.";

    private static final GameStateDTO FIRST_STATE = new GameStateDTO(
        List.of(new ActorDTO(FIRST_PLAYER_ID, ActorKind.PLAYER, 0, 0)), List.of(), null);
    private static final GameStateDTO SECOND_STATE = new GameStateDTO(
        List.of(new ActorDTO(SECOND_PLAYER_ID, ActorKind.PLAYER, 0, 0)), List.of(), null);

    @Mock
    private ConnectionRegistry registry;
    @Mock
    private GameEngine engine;
    @Mock
    private RequestMapper mapper;

    private RequestHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RequestHandler(registry, engine, mapper);
    }

    @Test
    void testHandleMovesThePlayerOnAMoveRequest() {
        when(mapper.deserialize(REQUEST)).thenReturn(new MoveRequest(Direction.UP));

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(engine).move(FIRST_PLAYER_ID, Direction.UP);
    }

    @Test
    void testHandleSelectsTheSlotOnASelectRequest() {
        when(mapper.deserialize(REQUEST)).thenReturn(new SelectRequest(SLOT));

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(engine).select(FIRST_PLAYER_ID, SLOT);
    }

    @Test
    void testHandleUsesTheSelectedItemOnAUseRequest() {
        when(mapper.deserialize(REQUEST)).thenReturn(new UseRequest(TARGET_ID));

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(engine).use(FIRST_PLAYER_ID, TARGET_ID);
    }

    @Test
    void testHandlePicksUpTheTreasureOnAPickUpRequest() {
        when(mapper.deserialize(REQUEST)).thenReturn(new PickUpRequest(TREASURE_ID));

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(engine).pickUp(FIRST_PLAYER_ID, TREASURE_ID);
    }

    @Test
    void testHandleGivesTheItemOnAGiveRequest() {
        when(mapper.deserialize(REQUEST)).thenReturn(new GiveRequest(SECOND_PLAYER_ID));

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(engine).give(FIRST_PLAYER_ID, SECOND_PLAYER_ID);
    }

    @Test
    void testHandleDropsTheItemOnADropRequest() {
        when(mapper.deserialize(REQUEST)).thenReturn(new DropRequest());

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(engine).drop(FIRST_PLAYER_ID);
    }

    @Test
    void testHandleUnregistersThePlayerOnAQuitRequest() {
        when(mapper.deserialize(REQUEST)).thenReturn(new QuitRequest());

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(registry).unregister(FIRST_PLAYER_ID);
    }

    @Test
    void testHandleTellsThePlayerWhenTheirRequestCannotBeRead() {
        when(mapper.deserialize(REQUEST)).thenThrow(new InvalidRequestException("Unreadable"));

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(registry).sendTo(FIRST_PLAYER_ID, new ErrorResponse(UNREADABLE_REQUEST));
        verifyNoInteractions(engine);
    }

    @Test
    void testHandleTellsThePlayerWhyTheirActionWasRefused() {
        when(mapper.deserialize(REQUEST)).thenReturn(new DropRequest());
        when(engine.drop(FIRST_PLAYER_ID)).thenThrow(new EmptySlotException(REFUSAL));

        handler.handle(FIRST_PLAYER_ID, REQUEST);

        verify(registry).sendTo(FIRST_PLAYER_ID, new ErrorResponse(REFUSAL));
    }

    @Test
    void testHandleTellsThePlayerWhenTheRequestCouldNotBeHandled() {
        when(mapper.deserialize(REQUEST)).thenReturn(new DropRequest());
        when(engine.drop(FIRST_PLAYER_ID)).thenThrow(new IllegalStateException("Broken"));

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
