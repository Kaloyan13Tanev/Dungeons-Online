package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.RequestRouter;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PlayerClient {

    private static final int SERVER_PORT = 4444;
    private static final Gson GSON = new Gson();

    static void main() {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            Thread.currentThread().setName("Echo client thread " + socket.getLocalPort());

            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();

                if ("quit".equals(message)) {
                    break;
                }

                System.out.println("Sending message <" + message + "> to the server...");

                Request request = new Request(message, socket.getInetAddress());
                writer.println(GSON.toJson(request));
            }
        } catch (IOException e) {
            throw new RuntimeException("There was a problem with the network communication", e);
        }
    }
}
