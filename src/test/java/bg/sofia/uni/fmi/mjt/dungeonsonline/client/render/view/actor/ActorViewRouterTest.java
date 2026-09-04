package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActorViewRouterTest {

    private static final int PLAYER_ID = 1;
    private static final int MINION_ID = 10;

    private static final int ROW = 3;
    private static final int COL = 4;

    private static final ActorDTO PLAYER = new ActorDTO(PLAYER_ID, ActorKind.PLAYER, ROW, COL);
    private static final ActorDTO MINION = new ActorDTO(MINION_ID, ActorKind.MINION, ROW, COL);

    @Mock
    private Function<ActorDTO, ActorView> view;
    @Mock
    private ActorView playerView;

    private ActorViewRouter router;

    @BeforeEach
    void setUp() {
        router = new ActorViewRouter();
    }

    @Test
    void testRouteReturnsTheViewTheRegisteredOneMadeOfTheActor() {
        when(view.apply(PLAYER)).thenReturn(playerView);
        router.register(ActorKind.PLAYER, view);

        assertSame(playerView, router.route(PLAYER),
            "ActorViewRouter should return the view the one registered for the kind made of the actor");
    }

    @Test
    void testRouteThrowsWhenTheKindHasNoRegisteredView() {
        router.register(ActorKind.PLAYER, view);

        assertThrows(IllegalStateException.class, () -> router.route(MINION),
            "ActorViewRouter should refuse an actor of a kind it has no view for");
    }

}
