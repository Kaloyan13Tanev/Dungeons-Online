package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.message.Message;
import bg.sofia.uni.fmi.mjt.dungeonsonline.message.MessageStatus;
import com.google.gson.Gson;

import java.io.PrintWriter;
import java.net.Socket;

public class FullLobbyHandler implements Runnable {

    private final Socket socket;

    public FullLobbyHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
            Message message = new Message(MessageStatus.CLIENT_REJECTED, "Server is full at the moment. " +
                    "Try joining again later.");
            Gson gson = new Gson();
            String toClient = gson.toJson(message);
            out.println(toClient);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
