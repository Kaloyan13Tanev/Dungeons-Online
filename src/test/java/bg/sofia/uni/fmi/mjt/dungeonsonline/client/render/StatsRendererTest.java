package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.PlayerStateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatsRendererTest {

    private static final int START_COLUMN = 1;

    private static final int LEVEL = 2;
    private static final int XP = 40;
    private static final int XP_CAP = 100;
    private static final int HEALTH = 70;
    private static final int MAX_HEALTH = 110;
    private static final int MANA = 60;
    private static final int MAX_MANA = 120;
    private static final int ATTACK = 55;
    private static final int DEFENSE = 45;
    private static final int SELECTED_SLOT = 0;

    private static final PlayerStateDTO PLAYER = new PlayerStateDTO(LEVEL, XP, XP_CAP, HEALTH,
        MAX_HEALTH, MANA, MAX_MANA, ATTACK, DEFENSE, List.of(), SELECTED_SLOT);

    private static final GameStateDTO STATE = new GameStateDTO(List.of(), List.of(), PLAYER);

    @Mock
    private Console console;
    @Mock
    private ClientState state;

    private StatsRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new StatsRenderer(console, START_COLUMN);
    }

    @Test
    void testRenderShowsTheLevelAndTheExperienceOfThePlayer() {
        when(state.getState()).thenReturn(STATE);

        renderer.render(state);

        String line = printedLine(1);

        assertTrue(line.contains("Level: " + LEVEL), "StatsRenderer should show the level of the player");
        assertTrue(line.contains("XP: " + XP + "/" + XP_CAP),
            "StatsRenderer should show the experience of the player against the cap");
    }

    @Test
    void testRenderShowsTheAttackDefenseHealthAndManaOfThePlayer() {
        when(state.getState()).thenReturn(STATE);

        renderer.render(state);

        String line = printedLine(2);

        assertTrue(line.contains("Attack: " + ATTACK), "StatsRenderer should show the attack of the player");
        assertTrue(line.contains("Defense: " + DEFENSE), "StatsRenderer should show the defense of the player");
        assertTrue(line.contains("Health: " + HEALTH + "/" + MAX_HEALTH),
            "StatsRenderer should show the health of the player against the maximum");
        assertTrue(line.contains("Mana: " + MANA + "/" + MAX_MANA),
            "StatsRenderer should show the mana of the player against the maximum");
    }

    @Test
    void testRenderClearsItsAreaBeforeItPrints() {
        when(state.getState()).thenReturn(STATE);

        renderer.render(state);

        verify(console).clearArea(anyInt(), anyInt(), anyInt(), anyInt());
        verify(console).flush();
    }

    private String printedLine(int line) {
        ArgumentCaptor<String> printed = ArgumentCaptor.forClass(String.class);
        verify(console, times(3)).print(printed.capture());

        return printed.getAllValues().get(line);
    }

}
