package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.item;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;
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
public class ItemViewRouterTest {

    private static final int LEVEL = 1;
    private static final int POWER = 10;
    private static final int MANA_COST = 5;

    private static final ItemDTO WEAPON = new ItemDTO(ItemKind.WEAPON, "Sword", LEVEL, POWER, 0);
    private static final ItemDTO SPELL = new ItemDTO(ItemKind.SPELL, "Spark", LEVEL, POWER, MANA_COST);

    @Mock
    private Function<ItemDTO, ItemView> view;
    @Mock
    private ItemView weaponView;

    private ItemViewRouter router;

    @BeforeEach
    void setUp() {
        router = new ItemViewRouter();
    }

    @Test
    void testRouteReturnsTheViewTheRegisteredOneMadeOfTheItem() {
        when(view.apply(WEAPON)).thenReturn(weaponView);
        router.register(ItemKind.WEAPON, view);

        assertSame(weaponView, router.route(WEAPON),
            "ItemViewRouter should return the view the one registered for the kind made of the item");
    }

    @Test
    void testRouteThrowsWhenTheKindHasNoRegisteredView() {
        router.register(ItemKind.WEAPON, view);

        assertThrows(IllegalStateException.class, () -> router.route(SPELL),
            "ItemViewRouter should refuse an item of a kind it has no view for");
    }

}
