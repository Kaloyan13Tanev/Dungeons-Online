package bg.sofia.uni.fmi.mjt.dungeonsonline.server.serialization;

import com.google.gson.Gson;
import org.example.common.response.Response;
import org.example.common.serialize.Serializer;

public final class ResponseSerializer implements Serializer<Response> {

    private static final Gson GSON = new Gson();

    @Override
    public String serialize(Response response) {
        String json = GSON.toJson(response);

        return String.valueOf(json.length()) +
                ' ' +
                json;
    }

}