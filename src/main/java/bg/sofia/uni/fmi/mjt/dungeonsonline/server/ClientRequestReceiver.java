package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.Request;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClientRequestReceiver implements Runnable {

    private static final Gson GSON = new Gson();

    private final InputStream inputStream;
    private final RequestRouter requestRouter;

    public ClientRequestReceiver(InputStream inputStream, RequestRouter requestRouter) {
        this.inputStream = inputStream;
        this.requestRouter = requestRouter;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) { // read the message from the client
                Request request = GSON.fromJson(inputLine, Request.class);
                System.out.println("Message received from client " + request.getPort() + ": " + inputLine);
                requestRouter.route(request);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
