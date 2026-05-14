package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.message.Message;
import bg.sofia.uni.fmi.mjt.dungeonsonline.message.MessageStatus;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientRequestHandler implements Runnable {

    private static final Message ACCEPTANCE_MESSAGE = new Message(MessageStatus.CLIENT_ACCEPTED,
            "You joined the game.");

    private final Socket socket;
    private AtomicInteger playerCount;

    public ClientRequestHandler(Socket socket, AtomicInteger playerCount) {
        this.socket = socket;
        this.playerCount = playerCount;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("Client Request Handler for " + socket.getRemoteSocketAddress());

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // autoflush on
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             socket) {

            sendAcceptanceMessageToClient(out);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Message received from client: " + inputLine);
                out.println("Echo " + inputLine);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            playerCount.decrementAndGet();
        }

    }

    private void sendAcceptanceMessageToClient(PrintWriter out) {
        Gson gson = new Gson();
        out.println(gson.toJson(ACCEPTANCE_MESSAGE));
    }

}