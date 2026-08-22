package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ResponseMapperTest {

    private static final String EVENT_MESSAGE = "Test message";

    private static final String RESPONSE1_JSON =
        "{\"type\":\"EVENT\",\"body\":\"{\\\"message\\\":\\\"Test message\\\"}\"}";

    private static final String NULL_TYPE_JSON =
        "{\"type\":null,\"body\":\"{\\\"message\\\":\\\"Test message\\\"}\"}";
    private static final String UNKNOWN_TYPE_JSON =
        "{\"type\":\"UNKNOWN\",\"body\":\"{\\\"message\\\":\\\"Test message\\\"}\"}";

    private static final String MISMATCHED_BODY_JSON = "{\"type\":\"EVENT\",\"body\":\"[1,2,3]\"}";
    private static final String EMPTY_BODY_JSON = "{\"type\":\"EVENT\",\"body\":\"\"}";

    private static final String NOT_JSON = "not JSON";
    private static final String TWO_RESPONSES_JSON =
        "{\"type\":\"EVENT\",\"body\":\"{}\"}{\"type\":\"ERROR\",\"body\":\"{}\"}";

    private final ResponseMapper mapper = new ResponseMapper();

    Response response1;

    @BeforeEach
    void setUp() {
        response1 = new EventResponse(EVENT_MESSAGE);
    }

    @Test
    void testSerializeReturnsValidJson() {
        String serialized = mapper.serialize(response1);

        assertEquals(RESPONSE1_JSON, serialized,
            "ResponseMapper should return a valid JSON string");
    }

    @Test
    void testDeserializeReturnsCorrectResponse() {
        Response deserialized = mapper.deserialize(RESPONSE1_JSON);

        assertEquals(response1, deserialized,
            "ResponseMapper should restore a response unchanged from its serialized form");
    }

    @Test
    void testDeserializeThrowsInvalidResponseExceptionWhenDTOIsNull() {
        assertThrows(InvalidResponseException.class, () -> mapper.deserialize(null),
            "ResponseMapper should throw when there is nothing to read");
    }

    @Test
    void testDeserializeThrowsInvalidResponseExceptionWhenTypeIsNullOrUnknown() {
        assertThrows(InvalidResponseException.class, () -> mapper.deserialize(NULL_TYPE_JSON),
            "ResponseMapper should throw when the response has no type");
        assertThrows(InvalidResponseException.class, () -> mapper.deserialize(UNKNOWN_TYPE_JSON),
            "ResponseMapper should throw when the response type is unknown");
    }

    @Test
    void testDeserializeThrowsInvalidResponseExceptionWhenBodyDoesNotMatch() {
        assertThrows(InvalidResponseException.class, () -> mapper.deserialize(MISMATCHED_BODY_JSON),
            "ResponseMapper should throw when the body does not fit the response type");
    }

    @Test
    void testDeserializeThrowsInvalidResponseExceptionWhenJsonInvalid() {
        assertThrows(InvalidResponseException.class, () -> mapper.deserialize(NOT_JSON),
            "ResponseMapper should throw when the input is not JSON");
        assertThrows(InvalidResponseException.class, () -> mapper.deserialize(EMPTY_BODY_JSON),
            "ResponseMapper should throw when the body is not JSON");
        assertThrows(InvalidResponseException.class, () -> mapper.deserialize(TWO_RESPONSES_JSON),
            "ResponseMapper should throw when the input holds more than one response");
    }

}
