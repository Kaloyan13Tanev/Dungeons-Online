package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.Response;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.ResponseType;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ResponseMapper {

    private static final Gson GSON = new Gson();

    public String serialize(Response response) {
        return toJson(toDTO(response));
    }

    public Response deserialize(String json) {
        return toResponse(fromJson(json));
    }

    private ResponseDTO toDTO(Response response) {
        return new ResponseDTO(ResponseType.of(response), GSON.toJson(response));
    }

    private Response toResponse(ResponseDTO dto) {
        if (dto == null || dto.type() == null) {
            throw new InvalidResponseException("Missing or unknown response type");
        }

        Response response;
        try {
            response = GSON.fromJson(dto.body(), dto.type().responseClass());
        } catch (JsonSyntaxException e) {
            throw new InvalidResponseException("Body does not match " + dto.type(), e);
        }

        if (response == null) {
            throw new InvalidResponseException("Empty body for " + dto.type());
        }

        return response;
    }

    private String toJson(ResponseDTO dto) {
        return GSON.toJson(dto);
    }

    private ResponseDTO fromJson(String json) {
        try {
            return GSON.fromJson(json, ResponseDTO.class);
        } catch (JsonSyntaxException e) {
            throw new InvalidResponseException("Invalid JSON", e);
        }
    }
}
