package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveCommandTest {

    private static final Direction DIRECTION = Direction.UP;

    @Test
    void testExecuteMakesAMoveRequestInItsDirection() {
        assertEquals(Optional.of(new MoveRequest(DIRECTION)), new MoveCommand(DIRECTION).execute(),
            "MoveCommand should ask the server to move the player in the direction it was made with");
    }

}
