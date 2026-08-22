package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientStateTest {

    private static final int MESSAGE_HISTORY = 6;

    private static final int MY_ID = 1;
    private static final int OTHER_PLAYER_ID = 2;
    private static final int MINION_ID = 10;

    private static final int MY_ROW = 3;
    private static final int MY_COL = 4;
    private static final int OTHER_ROW = 5;
    private static final int OTHER_COL = 6;

    private static final int FIRST_TREASURE_ID = 1;
    private static final int SECOND_TREASURE_ID = 2;

    private static final ActorDTO ME = new ActorDTO(MY_ID, ActorKind.PLAYER, MY_ROW, MY_COL);
    private static final ActorDTO OTHER_PLAYER_HERE =
        new ActorDTO(OTHER_PLAYER_ID, ActorKind.PLAYER, MY_ROW, MY_COL);
    private static final ActorDTO MINION_HERE = new ActorDTO(MINION_ID, ActorKind.MINION, MY_ROW, MY_COL);
    private static final ActorDTO MINION_ELSEWHERE =
        new ActorDTO(MINION_ID, ActorKind.MINION, OTHER_ROW, OTHER_COL);

    private static final TreasureDTO TREASURE_HERE =
        new TreasureDTO(FIRST_TREASURE_ID, null, MY_ROW, MY_COL);
    private static final TreasureDTO TREASURE_ELSEWHERE =
        new TreasureDTO(SECOND_TREASURE_ID, null, OTHER_ROW, OTHER_COL);

    private static final String MESSAGE = "You hit a minion for 15 damage.";
    private static final String ERROR = "You have nothing to drop!";

    private ClientState state;

    @BeforeEach
    void setUp() {
        state = new ClientState();
        state.setPlayerId(MY_ID);
    }

    @Test
    void testAddMessageHoldsEveryMessageItWasGiven() {
        state.addMessage(MESSAGE);
        state.addError(ERROR);

        List<Message> messages = state.getMessages();

        assertEquals(2, messages.size(), "ClientState should hold every message it was given");
        assertTrue(messages.containsAll(List.of(new Message(MESSAGE, false), new Message(ERROR, true))),
            "ClientState should hold the messages it was given");
    }

    @Test
    void testAddErrorMarksTheMessageAsAnError() {
        state.addMessage(MESSAGE);
        state.addError(ERROR);

        assertFalse(state.getMessages().getFirst().error(),
            "ClientState should not mark a plain message as an error");
        assertTrue(state.getMessages().getLast().error(),
            "ClientState should mark a message added as an error");
    }

    @Test
    void testAddMessageDropsTheOldestMessageOnceTheHistoryIsFull() {
        for (int message = 0; message < MESSAGE_HISTORY; message++) {
            state.addMessage(MESSAGE + message);
        }

        state.addMessage(ERROR);

        List<Message> messages = state.getMessages();

        assertEquals(MESSAGE_HISTORY, messages.size(), "ClientState should never hold more messages than it keeps");
        assertEquals(MESSAGE + 1, messages.getFirst().text(),
            "ClientState should drop the oldest message when a new one does not fit");
        assertEquals(ERROR, messages.getLast().text(), "ClientState should keep the message that arrived last");
    }

    @Test
    void testGetSelfReturnsTheActorWithTheIdOfThePlayer() {
        state.setState(new GameStateDTO(List.of(MINION_HERE, ME), List.of(), null));

        assertEquals(ME, state.getSelf().orElseThrow(),
            "ClientState should return the actor that carries the id of the player");
    }

    @Test
    void testGetSelfReturnsEmptyBeforeAnyStateArrives() {
        assertTrue(state.getSelf().isEmpty(),
            "ClientState should return empty while it has no state to look in");
    }

    @Test
    void testGetSelfReturnsEmptyWhenThePlayerIsNotAmongTheActors() {
        state.setState(new GameStateDTO(List.of(MINION_HERE), List.of(), null));

        assertTrue(state.getSelf().isEmpty(),
            "ClientState should return empty when no actor carries the id of the player");
    }

    @Test
    void testActorsOnMyTileReturnsTheOthersStandingOnIt() {
        state.setState(new GameStateDTO(List.of(ME, OTHER_PLAYER_HERE, MINION_HERE, MINION_ELSEWHERE),
            List.of(), null));

        List<ActorDTO> onTile = state.actorsOnMyTile();

        assertEquals(2, onTile.size(), "ClientState should return every other actor standing on the tile");
        assertTrue(onTile.containsAll(List.of(OTHER_PLAYER_HERE, MINION_HERE)),
            "ClientState should return the actors standing on the tile of the player");
        assertFalse(onTile.contains(ME), "ClientState should not return the player among the actors on their tile");
    }

    @Test
    void testPlayersOnMyTileLeavesOutTheMinions() {
        state.setState(new GameStateDTO(List.of(ME, OTHER_PLAYER_HERE, MINION_HERE), List.of(), null));

        assertEquals(List.of(OTHER_PLAYER_HERE), state.playersOnMyTile(),
            "ClientState should return only the players standing on the tile of the player");
    }

    @Test
    void testTreasuresOnMyTileReturnsTheOnesLyingOnIt() {
        state.setState(new GameStateDTO(List.of(ME), List.of(TREASURE_HERE, TREASURE_ELSEWHERE), null));

        assertEquals(List.of(TREASURE_HERE), state.treasuresOnMyTile(),
            "ClientState should return only the treasures lying on the tile of the player");
    }

}
