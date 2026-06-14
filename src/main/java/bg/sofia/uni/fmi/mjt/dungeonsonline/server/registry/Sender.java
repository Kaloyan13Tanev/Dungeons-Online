package bg.sofia.uni.fmi.mjt.dungeonsonline.server.registry;

import java.io.BufferedWriter;
import java.io.IOException;

public class Sender implements Runnable {

    private final String response;
    private final BufferedWriter writer;

    public Sender(String response, BufferedWriter writer) {
        this.response = response;
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            writer.write(response);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
