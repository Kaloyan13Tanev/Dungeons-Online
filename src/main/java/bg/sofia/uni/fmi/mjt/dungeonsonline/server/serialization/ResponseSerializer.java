package bg.sofia.uni.fmi.mjt.dungeonsonline.server.serialization;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.serialize.Serializer;
import com.google.gson.Gson;

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