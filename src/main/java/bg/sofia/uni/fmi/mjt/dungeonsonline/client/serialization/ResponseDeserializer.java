package bg.sofia.uni.fmi.mjt.dungeonsonline.client.serialization;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.serialize.Deserializer;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;

public final class ResponseDeserializer implements Deserializer<Response> {

    private static final Gson GSON = new Gson();

    private final BufferedReader reader;

    public ResponseDeserializer(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public Response deserialize() throws IOException {
        Integer length = readLength();
        if (length == null) {
            return null;
        }

        char[] buffer = new char[length];
        readFully(buffer, length);
        String json = new String(buffer);

        return GSON.fromJson(json, Response.class);
    }

    private Integer readLength() throws IOException {
        String lengthString = readToken();
        if (lengthString == null) {
            return null;
        }

        return Integer.parseInt(lengthString);
    }

    private String readToken() throws IOException {
        int c = reader.read();
        if (c == -1) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        while (c != -1 && c != ' ') {
            sb.append((char) c);
            c = reader.read();
        }
        if (c == -1) {
            throw new IOException("Stream ended while trying to retrieve data");
        }
        return sb.toString();
    }

    private void readFully(char[] buffer, int length) throws IOException {
        int total = 0;
        while (total < length) {
            int read = reader.read(buffer, total, length - total);
            if (read == -1) {
                throw new IOException("Stream ended while trying to retrieve JSON");
            }
            total += read;
        }
    }
}