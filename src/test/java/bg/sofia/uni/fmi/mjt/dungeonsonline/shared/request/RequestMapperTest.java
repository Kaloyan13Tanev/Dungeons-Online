package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestMapperTest {

    private static final String REQUEST1_JSON =
        "{\"type\":\"MOVE\",\"body\":\"{\\\"direction\\\":\\\"UP\\\"}\"}";

    private static final String NULL_TYPE_JSON =
        "{\"type\":null,\"body\":\"{\\\"direction\\\":\\\"UP\\\"}\"}";
    private static final String UNKNOWN_TYPE_JSON =
        "{\"type\":\"UNKNOWN\",\"body\":\"{\\\"direction\\\":\\\"UP\\\"}\"}";

    private static final String MISMATCHED_BODY_JSON = "{\"type\":\"MOVE\",\"body\":\"[1,2,3]\"}";
    private static final String EMPTY_BODY_JSON = "{\"type\":\"MOVE\",\"body\":\"\"}";

    private static final String NOT_JSON = "not JSON";
    private static final String TWO_REQUESTS_JSON =
        "{\"type\":\"MOVE\",\"body\":\"{}\"}{\"type\":\"QUIT\",\"body\":\"{}\"}";

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
        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(NULL_TYPE_JSON),
            "RequestMapper should throw when the request has no type");
        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(UNKNOWN_TYPE_JSON),
            "RequestMapper should throw when the request type is unknown");
    }

    @Test
    void testDeserializeThrowsInvalidRequestExceptionWhenBodyDoesNotMatch() {
        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(MISMATCHED_BODY_JSON),
            "RequestMapper should throw when the body does not fit the request type");
    }

    @Test
    void testDeserializeThrowsInvalidRequestExceptionWhenJsonInvalid() {
        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(NOT_JSON),
            "RequestMapper should throw when the input is not JSON");
        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(EMPTY_BODY_JSON),
            "RequestMapper should throw when the body is not JSON");
        assertThrows(InvalidRequestException.class, () -> mapper.deserialize(TWO_REQUESTS_JSON),
            "RequestMapper should throw when the input holds more than one request");
    }

}
