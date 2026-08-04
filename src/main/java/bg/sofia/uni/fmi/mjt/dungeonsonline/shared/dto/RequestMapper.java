package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.RequestType;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class RequestMapper {

    private static final Gson GSON = new Gson();

    public String serialize(Request request) {
        return toJson(toDTO(request));
    }

    public Request deserialize(String json) {
        return toRequest(fromJson(json));
    }

    private RequestDTO toDTO(Request request) {
        return new RequestDTO(RequestType.of(request), GSON.toJson(request));
    }

    private Request toRequest(RequestDTO dto) {
        if (dto == null || dto.type() == null) {
            throw new InvalidRequestException("Missing or unknown request type");
        }

        Request request;
        try {
            request = GSON.fromJson(dto.body(), dto.type().requestClass());
        } catch (JsonSyntaxException e) {
            throw new InvalidRequestException("Body does not match " + dto.type(), e);
        }

        if (request == null) {
            throw new InvalidRequestException("Empty body for " + dto.type());
        }

        return request;
    }

    private String toJson(RequestDTO dto) {
        return GSON.toJson(dto);
    }

    private RequestDTO fromJson(String json) {
        try {
            return GSON.fromJson(json, RequestDTO.class);
        } catch (JsonSyntaxException e) {
            throw new InvalidRequestException("Invalid JSON", e);
        }
    }
}
