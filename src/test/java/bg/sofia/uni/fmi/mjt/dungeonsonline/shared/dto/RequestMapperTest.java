package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestMapperTest {

    private static final String REQUEST1_JSON = "{\"type\":\"MOVE\",\"body\":\"{\\\"direction\\\":\\\"UP\\\"}\"}";

    private final RequestMapper mapper = new RequestMapper();

    Request request1;

    @BeforeEach
    void setUp() {
        request1 = new MoveRequest(Direction.UP);
    }

    @Test
    void testSerializeReturnsValidJson() {
        String serialized = mapper.serialize(request1);

        assertEquals(REQUEST1_JSON, serialized,
            "RequestMapper should return a valid JSON string");
    }

    @Test
    void testDeserializeReturnsCorrectRequest() {
        Request deserialized = mapper.deserialize(REQUEST1_JSON);

        assertEquals(request1, deserialized,
            "RequestMapper should restore a request unchanged from its serialized form");
    }

    @Test
    void testDeserializeThrowsInvalidRequestExceptionWhenDTOIsNull() {
        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(null),
            "RequestMapper should throw when there is nothing to read");
    }

    @Test
    void testDeserializeThrowsInvalidRequestExceptionWhenTypeIsNullOrUnknown() {
        String nullType = "{\"type\":null,\"body\":\"{\\\"direction\\\":\\\"UP\\\"}\"}";
        String unknownType = "{\"type\":\"UNKNOWN\",\"body\":\"{\\\"direction\\\":\\\"UP\\\"}\"}";

        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(nullType),
            "RequestMapper should throw when the request has no type");
        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(unknownType),
            "RequestMapper should throw when the request type is unknown");
    }

    @Test
    void testDeserializeThrowsInvalidRequestExceptionWhenBodyDoesNotMatch() {
        String mismatchedBody = "{\"type\":\"MOVE\",\"body\":\"[1,2,3]\"}";

        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(mismatchedBody),
            "RequestMapper should throw when the body does not fit the request type");
    }

    @Test
    void testDeserializeThrowsInvalidRequestExceptionWhenJsonInvalid() {
        String notJson = "not JSON";
        String emptyBody = "{\"type\":\"MOVE\",\"body\":\"\"}";
        String extraParts = "{\"type\":\"MOVE\",\"body\":\"{}\"}{\"type\":\"QUIT\",\"body\":\"{}\"}";

        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(notJson),
            "RequestMapper should throw when the input is not JSON");
        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(emptyBody),
            "RequestMapper should throw when the body is not JSON");
        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(extraParts),
            "RequestMapper should throw when the input holds more than one request");
    }

}
