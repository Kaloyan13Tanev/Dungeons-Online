package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.PlayerStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BackpackRendererTest {

    private static final int START_ROW = 5;
    private static final int START_COLUMN = 1;

    private static final String SELECTED_MARK = "> ";
    private static final String UNSELECTED_MARK = "  ";

    private static final int FIRST_SLOT = 0;
    private static final int SECOND_SLOT = 1;

    private static final ItemDTO SWORD = new ItemDTO(ItemKind.WEAPON, "Sword", 1, 20, 0);

    private static final String SWORD_TEXT = "[Sword]";
    private static final String EMPTY_TEXT = "[EMPTY]";

    @Mock
    private Console console;
    @Mock
    private ClientState state;
    @Mock
    private ItemFormatter items;

    private BackpackRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new BackpackRenderer(console, items, START_COLUMN);
    }

    @Test
    void testRenderShowsEverySlotOfTheBackpack() {
        mockBackpack(SECOND_SLOT);
        when(items.format(SWORD)).thenReturn(SWORD_TEXT);
        when(items.format((ItemDTO) null)).thenReturn(EMPTY_TEXT);

        renderer.render(state);

        verify(console).print(SWORD_TEXT);
        verify(console).print(EMPTY_TEXT);
    }

    @Test
    void testRenderMarksTheSelectedSlot() {
        mockBackpack(FIRST_SLOT);

        renderer.render(state);

        verify(console).print(SELECTED_MARK);
        verify(console).print(UNSELECTED_MARK);
    }

    @Test
    void testRenderMarksOnlyTheSlotThePlayerSelected() {
        mockBackpack(SECOND_SLOT);

        renderer.render(state);

        verify(console, times(1)).print(SELECTED_MARK);
    }

    @Test
    void testRenderShowsEverySlotOnItsOwnLine() {
        mockBackpack(FIRST_SLOT);

        renderer.render(state);

        verify(console).moveCursor(START_ROW + 1, START_COLUMN);
        verify(console).moveCursor(START_ROW + 2, START_COLUMN);
    }
    
    @Test
    void testRenderClearsItsAreaBeforeItPrints() {
        mockBackpack(FIRST_SLOT);

        renderer.render(state);

        verify(console).clearArea(anyInt(), anyInt(), anyInt(), anyInt());
        verify(console).flush();
    }

    private void mockBackpack(int selectedSlot) {
        List<ItemDTO> backpack = Arrays.asList(SWORD, null);
        PlayerStateDTO player = new PlayerStateDTO(1, 0, 100, 100, 100, 100, 100, 50, 50,
            backpack, selectedSlot);

        when(state.getState()).thenReturn(new GameStateDTO(List.of(), List.of(), player));
    }

}
