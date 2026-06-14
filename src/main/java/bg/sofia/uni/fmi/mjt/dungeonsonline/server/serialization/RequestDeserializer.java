package bg.sofia.uni.fmi.mjt.dungeonsonline.server.serialization;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.serialize.Deserializer;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class RequestDeserializer implements Deserializer<Request> {

    private static final Gson GSON = new Gson();

    private final BufferedReader in;
    private final Map<String, Class<? extends Request>> registry = buildRegistry();

    public RequestDeserializer(InputStream inputStream) {
        this.in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    @Override
    public Request deserialize() throws IOException {
        String className = readClassName();
        if (className == null) {
            return null;
        }

        int length = readLength();

        char[] buffer = new char[length];
        readFully(buffer, length);
        String json = new String(buffer);

        Class<? extends Request> type = registry.get(className);
        if (type == null) {
            throw new IOException("Unknown request type: " + className);
        }
        return GSON.fromJson(json, type);
    }

    private String readClassName() throws IOException {
        return readToken();
    }

    private int readLength() throws IOException {
        String lengthString = readToken();
        if (lengthString == null) {
            throw new IOException("Stream ended after class name");
        }

        return Integer.parseInt(lengthString);
    }

    private String readToken() throws IOException {
        int c = in.read();
        if (c == -1) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        while (c != -1 && c != ' ') {
            sb.append((char) c);
            c = in.read();
        }
        if (c == -1) {
            throw new IOException("Stream ended while trying to retrieve data");
        }
        return sb.toString();
    }

    private void readFully(char[] buffer, int length) throws IOException {
        int total = 0;
        while (total < length) {
            int read = in.read(buffer, total, length - total);
            if (read == -1) {
                throw new IOException("Stream ended while trying to retrieve JSON");
            }
            total += read;
        }
    }

    private Map<String, Class<? extends Request>> buildRegistry() {
        Map<String, Class<? extends Request>> map = new HashMap<>();
        for (Class<?> sub : Request.class.getPermittedSubclasses()) {
            map.put(sub.getSimpleName(), sub.asSubclass(Request.class));
        }
        return map;
    }
}