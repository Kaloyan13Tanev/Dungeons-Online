package bg.sofia.uni.fmi.mjt.dungeonsonline.client.channel;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.serialization.ResponseDeserializer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientReceiveChannel implements Runnable {

    private final BufferedReader reader;

    public ClientReceiveChannel(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        ResponseDeserializer deserializer = new ResponseDeserializer(reader);
        try {
            Response response;
            while ((response = deserializer.deserialize()) != null) {
                System.out.println(response.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
