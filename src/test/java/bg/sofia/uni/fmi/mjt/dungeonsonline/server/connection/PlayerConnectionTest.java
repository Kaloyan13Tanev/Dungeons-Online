package bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerConnectionTest {

    private static final int PLAYER_ID = 1;

    private static final String MESSAGE = "serialized response";
    private static final String FIRST_REQUEST = "first request";
    private static final String SECOND_REQUEST = "second request";

    @Mock
    private Socket socket;
    @Mock
    private BufferedReader reader;
    @Mock
    private BufferedWriter writer;
    @Mock
    private Consumer<String> onRequest;

    private PlayerConnection connection;

    @BeforeEach
    void setUp() {
        connection = new PlayerConnection(PLAYER_ID, socket, reader, writer);
    }

    @Test
    void testSendEndsTheMessageWithANewLineBeforeItFlushes() throws IOException {
        connection.send(MESSAGE);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).write(MESSAGE);
        inOrder.verify(writer).newLine();
        inOrder.verify(writer).flush();
    }

    @Test
    void testSendClosesTheConnectionWhenItCannotWrite() throws IOException {
        doThrow(new IOException("Broken pipe")).when(writer).write(MESSAGE);

        connection.send(MESSAGE);

        assertFalse(connection.isOpen(),
            "PlayerConnection should no longer be open once a message could not be sent");
        verify(socket).close();
    }

    @Test
    void testListenHandsEveryRequestItReadsToTheConsumer() throws IOException {
        when(reader.readLine()).thenReturn(FIRST_REQUEST, SECOND_REQUEST, null);

        connection.listen(onRequest);

        verify(onRequest).accept(FIRST_REQUEST);
        verify(onRequest).accept(SECOND_REQUEST);
    }

    @Test
    void testCloseClosesTheSocketOnlyOnce() throws IOException {
        connection.close();
        connection.close();

        verify(socket, times(1)).close();
        assertFalse(connection.isOpen(),
            "PlayerConnection should no longer be open once it was closed");
    }

    @Test
    void testCloseSurvivesASocketThatFailsToClose() throws IOException {
        doThrow(new IOException("Broken pipe")).when(socket).close();

        assertDoesNotThrow(() -> connection.close(),
            "PlayerConnection should not fail when the socket cannot be closed");
    }

}
