package bg.sofia.uni.fmi.mjt.dungeonsonline.client.serialization;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.serialize.Serializer;
import com.google.gson.Gson;

public final class RequestSerializer implements Serializer<Request> {

    private static final Gson GSON = new Gson();

    @Override
    public String serialize(Request request) {
        String json = GSON.toJson(request);

        return request.getClass().getSimpleName() +
                ' ' +
                json.length() +
                ' ' +
                json;
    }
}