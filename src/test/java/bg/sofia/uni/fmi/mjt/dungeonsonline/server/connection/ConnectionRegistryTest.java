package bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ResponseMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.EventResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConnectionRegistryTest {

    private static final int FIRST_PLAYER_ID = 1;
    private static final int SECOND_PLAYER_ID = 2;
    private static final int UNKNOWN_PLAYER_ID = 9;

    private static final Response RESPONSE = new EventResponse("Test message");
    private static final String SERIALIZED = "serialized response";

    @Mock
    private PlayerConnection firstConnection;
    @Mock
    private PlayerConnection secondConnection;
    @Mock
    private ResponseMapper mapper;

    private Map<Integer, PlayerConnection> connections;
    private ConnectionRegistry registry;

    @BeforeEach
    void setUp() {
        connections = new HashMap<>();
        registry = new ConnectionRegistry(connections, mapper);
    }

    @Test
    void testRegisterStoresEachConnectionUnderItsPlayerId() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(SECOND_PLAYER_ID);

        registry.register(firstConnection);
        registry.register(secondConnection);

        assertEquals(2, connections.size(), "ConnectionRegistry should hold every connection it registered");
        assertEquals(firstConnection, connections.get(FIRST_PLAYER_ID),
            "ConnectionRegistry should store a connection under the id it reports");
        assertEquals(secondConnection, connections.get(SECOND_PLAYER_ID),
            "ConnectionRegistry should store a connection under the id it reports");
    }

    @Test
    void testRegisterThrowsWhenIdIsAlreadyRegistered() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(FIRST_PLAYER_ID);

        registry.register(firstConnection);

        assertThrows(IllegalStateException.class, () -> registry.register(secondConnection),
            "ConnectionRegistry should throw when the id of a connection is already registered");
        assertEquals(firstConnection, connections.get(FIRST_PLAYER_ID),
            "ConnectionRegistry should keep the connection that holds the id");
        verify(secondConnection).close();
        verify(firstConnection, never()).close();
    }

    @Test
    void testUnregisterRemovesTheConnectionAndClosesIt() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(SECOND_PLAYER_ID);

        registry.register(firstConnection);
        registry.register(secondConnection);

        registry.unregister(FIRST_PLAYER_ID);

        assertEquals(1, connections.size(),
            "ConnectionRegistry should no longer hold a connection it unregistered");
        assertEquals(secondConnection, connections.get(SECOND_PLAYER_ID),
            "ConnectionRegistry should keep the connections of the other players");
        verify(firstConnection).close();
        verify(secondConnection, never()).close();
    }

    @Test
    void testUnregisterIgnoresAnUnknownId() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);

        registry.register(firstConnection);

        registry.unregister(UNKNOWN_PLAYER_ID);

        assertEquals(1, connections.size(),
            "ConnectionRegistry should not change when an unknown id is unregistered");
        verify(firstConnection, never()).close();
    }

    @Test
    void testSendToDeliversTheSerializedResponseToPlayer() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(SECOND_PLAYER_ID);
        when(mapper.serialize(RESPONSE)).thenReturn(SERIALIZED);

        registry.register(firstConnection);
        registry.register(secondConnection);

        registry.sendTo(FIRST_PLAYER_ID, RESPONSE);

        verify(firstConnection).send(SERIALIZED);
        verify(secondConnection, never()).send(any());
    }

    @Test
    void testSendToIgnoresAnUnknownId() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(mapper.serialize(RESPONSE)).thenReturn(SERIALIZED);

        registry.register(firstConnection);

        registry.sendTo(UNKNOWN_PLAYER_ID, RESPONSE);

        verify(firstConnection, never()).send(any());
    }

    @Test
    void testSendToACollectionDeliversTheSameMessageToEveryPlayerInIt() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(SECOND_PLAYER_ID);
        when(mapper.serialize(RESPONSE)).thenReturn(SERIALIZED);

        registry.register(firstConnection);
        registry.register(secondConnection);

        registry.sendTo(List.of(FIRST_PLAYER_ID, SECOND_PLAYER_ID), RESPONSE);

        verify(firstConnection).send(SERIALIZED);
        verify(secondConnection).send(SERIALIZED);
        verify(mapper).serialize(RESPONSE);
    }

    @Test
    void testSendToACollectionSkipsUnknownIds() {
        when(firstConnection.playerId()).thenReturn(FIRST_PLAYER_ID);
        when(secondConnection.playerId()).thenReturn(SECOND_PLAYER_ID);
        when(mapper.serialize(RESPONSE)).thenReturn(SERIALIZED);

        registry.register(firstConnection);
        registry.register(secondConnection);

        registry.sendTo(List.of(FIRST_PLAYER_ID, UNKNOWN_PLAYER_ID), RESPONSE);

        verify(firstConnection).send(SERIALIZED);
        verify(secondConnection, never()).send(any());
    }

}
