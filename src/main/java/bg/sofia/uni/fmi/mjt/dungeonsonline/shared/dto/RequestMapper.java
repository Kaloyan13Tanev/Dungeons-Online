package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.RequestType;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class RequestMapper {

    private final Gson gson;

    public RequestMapper() {
        this(new Gson());
    }

    public RequestMapper(Gson gson) {
        this.gson = gson;
    }

    public String serialize(Request request) {
        RequestDTO dto = new RequestDTO(RequestType.of(request), gson.toJson(request));

        return gson.toJson(dto);
    }

    public Request deserialize(String json) {
        RequestDTO dto;
        try {
            dto = gson.fromJson(json, RequestDTO.class);
        } catch (JsonSyntaxException e) {
            throw new InvalidRequestException("Invalid JSON", e);
        }

        if (dto == null || dto.type() == null) {
            throw new InvalidRequestException("Missing or unknown request type");
        }

        try {
            Request request = gson.fromJson(dto.body(), dto.type().requestClass());
            if (request == null) {
                throw new InvalidRequestException("Empty body for " + dto.type());
            }

            return request;
        } catch (JsonSyntaxException e) {
            throw new InvalidRequestException("Body does not match " + dto.type(), e);
        }
    }
}
