package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.terrain;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.TerrainKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TerrainViewRouterTest {

    @Mock
    private TerrainView groundView;

    private TerrainViewRouter router;

    @BeforeEach
    void setUp() {
        router = new TerrainViewRouter();
    }

    @Test
    void testRouteReturnsTheViewRegisteredForTheKind() {
        router.register(TerrainKind.GROUND, groundView);

        assertSame(groundView, router.route(TerrainKind.GROUND),
            "TerrainViewRouter should return the view registered for the kind of the terrain");
    }

    @Test
    void testRouteThrowsWhenTheKindHasNoRegisteredView() {
        router.register(TerrainKind.GROUND, groundView);

        assertThrows(IllegalStateException.class, () -> router.route(TerrainKind.OBSTACLE),
            "TerrainViewRouter should refuse a terrain of a kind it has no view for");
    }

}
