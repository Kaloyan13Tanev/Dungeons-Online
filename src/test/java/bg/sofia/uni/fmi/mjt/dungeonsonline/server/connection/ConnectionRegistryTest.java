package bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConnectionRegistryTest {

    private static final int FIRST_PLAYER_ID = 1;
    private static final int SECOND_PLAYER_ID = 2;
    private static final int UNKNOWN_PLAYER_ID = 9;
    private static final String MESSAGE = "Test message";

    @Mock
    private PlayerConnection firstConnection;
    @Mock
    private PlayerConnection secondConnection;

    private Map<Integer, PlayerConnection> connections;
    private ConnectionRegistry registry;

    @BeforeEach
    void setUp() {
        connections = new HashMap<>();
        registry = new ConnectionRegistry(connections);
    }

    @Test
    void testRegisterAddsNewConnection() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(SECOND_PLAYER_ID);

        registry.register(firstConnection);
        registry.register(secondConnection);

        assertEquals(2, connections.size());
        assertEquals(firstConnection, connections.get(FIRST_PLAYER_ID));
        assertEquals(secondConnection, connections.get(SECOND_PLAYER_ID));
    }

    @Test
    void testRegisterClosesConnectionAndThrowsIllegalStateExceptionWhenIdAlreadyConnected() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(FIRST_PLAYER_ID);

        registry.register(firstConnection);

        assertThrows(IllegalStateException.class, () -> registry.register(secondConnection));
        verify(secondConnection).close();
    }

    @Test
    void testUnregisterRemovesConnection() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(SECOND_PLAYER_ID);

        registry.register(firstConnection);
        registry.register(secondConnection);

        registry.unregister(FIRST_PLAYER_ID);

        verify(firstConnection).close();
        assertEquals(1, connections.size(),
            "ConnectionRegistry should remove connection when unregister is called");
        assertNull(connections.get(FIRST_PLAYER_ID),
            "ConnectionRegistry should no longer hold an unregistered connection");
        assertEquals(secondConnection, connections.get(SECOND_PLAYER_ID),
            "ConnectionRegistry should keep the connections of the other players");
    }

    @Test
    void testUnregisterIgnoresUnknownId() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);

        registry.register(firstConnection);

        registry.unregister(UNKNOWN_PLAYER_ID);

        assertEquals(1, connections.size(),
            "ConnectionRegistry should not change when an unknown id is unregistered");
        verify(firstConnection, never()).close();
    }

    @Test
    void testRegisterAcceptsIdThatWasUnregistered() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(FIRST_PLAYER_ID);

        registry.register(firstConnection);
        registry.unregister(FIRST_PLAYER_ID);

        registry.register(secondConnection);
        registry.sendTo(FIRST_PLAYER_ID, MESSAGE);

        assertEquals(secondConnection, connections.get(FIRST_PLAYER_ID),
            "ConnectionRegistry should let a freed id be registered again");
        verify(secondConnection).send(MESSAGE);
    }

    @Test
    void testSendToDeliversToTheGivenPlayer() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);

        registry.register(firstConnection);

        registry.sendTo(FIRST_PLAYER_ID, MESSAGE);

        verify(firstConnection).send(MESSAGE);
    }

    @Test
    void testSendToIgnoresUnknownId() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);

        registry.register(firstConnection);

        registry.sendTo(UNKNOWN_PLAYER_ID, MESSAGE);

        verify(firstConnection, never()).send(MESSAGE);
    }

    @Test
    void testSendToACollectionDeliversToEachGivenPlayer() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(SECOND_PLAYER_ID);

        registry.register(firstConnection);
        registry.register(secondConnection);

        registry.sendTo(List.of(FIRST_PLAYER_ID, UNKNOWN_PLAYER_ID), MESSAGE);

        verify(firstConnection).send(MESSAGE);
        verify(secondConnection, never()).send(MESSAGE);
    }

    @Test
    void testSendToAllDeliversToEveryRegisteredPlayer() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(SECOND_PLAYER_ID);

        registry.register(firstConnection);
        registry.register(secondConnection);

        registry.sendToAll(MESSAGE);

        verify(firstConnection).send(MESSAGE);
        verify(secondConnection).send(MESSAGE);
    }

}
