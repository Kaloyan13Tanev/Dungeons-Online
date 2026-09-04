package bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEvent;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.RequestType;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.SelectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouterTest {

    private static final int PLAYER_ID = 1;
    private static final int SLOT = 3;

    private static final String MESSAGE = "Player 1 moved.";

    private static final MoveRequest MOVE = new MoveRequest(Direction.UP);

    private static final List<GameEvent> EVENTS = List.of(new GameEvent(Set.of(PLAYER_ID), MESSAGE));

    @Mock
    private BiFunction<Integer, MoveRequest, List<GameEvent>> action;

    private Router router;

    @BeforeEach
    void setUp() {
        router = new Router();
    }

    @Test
    void testRouteReturnsTheEventsOfTheActionRegisteredForTheRequest() {
        when(action.apply(PLAYER_ID, MOVE)).thenReturn(EVENTS);
        router.register(RequestType.MOVE, MoveRequest.class, action);

        assertEquals(EVENTS, router.route(PLAYER_ID, MOVE),
            "Router should return the events of the action registered for the type of the request");
    }

    @Test
    void testRouteThrowsWhenTheRequestHasNoRegisteredAction() {
        router.register(RequestType.MOVE, MoveRequest.class, action);

        assertThrows(IllegalStateException.class, () -> router.route(PLAYER_ID, new SelectRequest(SLOT)),
            "Router should refuse a request whose type it has no action for");
    }

}
