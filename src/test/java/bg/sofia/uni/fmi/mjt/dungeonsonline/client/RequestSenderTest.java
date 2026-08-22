package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.RequestMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedWriter;
import java.io.IOException;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestSenderTest {

    private static final Request REQUEST = new MoveRequest(Direction.UP);
    private static final String SERIALIZED = "serialized request";

    @Mock
    private BufferedWriter writer;
    @Mock
    private RequestMapper mapper;

    private RequestSender sender;

    @BeforeEach
    void setUp() {
        sender = new RequestSender(writer, mapper);
    }

    @Test
    void testSendWritesTheSerializedRequest() throws IOException {
        when(mapper.serialize(REQUEST)).thenReturn(SERIALIZED);

        sender.send(REQUEST);

        verify(writer).write(SERIALIZED);
    }

    @Test
    void testSendEndsTheRequestWithANewLineBeforeItFlushes() throws IOException {
        when(mapper.serialize(REQUEST)).thenReturn(SERIALIZED);

        sender.send(REQUEST);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).write(SERIALIZED);
        inOrder.verify(writer).newLine();
        inOrder.verify(writer).flush();
    }

}
