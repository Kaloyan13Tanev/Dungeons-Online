package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.SelectRequest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectSlotCommandTest {

    private static final int SLOT = 3;

    @Test
    void testExecuteMakesASelectRequestForItsSlot() {
        assertEquals(Optional.of(new SelectRequest(SLOT)), new SelectSlotCommand(SLOT).execute(),
            "SelectSlotCommand should ask the server to select the slot it was made with");
    }

}
