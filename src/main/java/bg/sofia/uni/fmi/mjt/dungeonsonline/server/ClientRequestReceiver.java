package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClientRequestReceiver implements Runnable {

    private final InputStream inputStream;

    public ClientRequestReceiver(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) { // read the message from the client
                System.out.println("Message received from client: " + inputLine);
                //requestRouter.route(inputLine);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
