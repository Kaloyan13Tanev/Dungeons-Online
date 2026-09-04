package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.DropRequest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropCommandTest {

    @Test
    void testExecuteMakesADropRequest() {
        assertEquals(Optional.of(new DropRequest()), new DropCommand().execute(),
            "DropCommand should ask the server to drop the item the player has selected");
    }

}
