package bg.sofia.uni.fmi.mjt.dungeonsonline.server.communication;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.serialization.ResponseSerializer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class RejectedPlayer implements Runnable {

    private static final Response REJECT_RESPONSE = new Response("Server is full. Try again later.");

    private final Socket socket;

    public RejectedPlayer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ResponseSerializer serializer = new ResponseSerializer();

        try (socket;
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            writer.write(serializer.serialize(REJECT_RESPONSE));
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
