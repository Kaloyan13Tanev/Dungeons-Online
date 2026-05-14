package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.message.Message;
import bg.sofia.uni.fmi.mjt.dungeonsonline.message.MessageStatus;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PlayerClient {

    private static final int SERVER_PORT = 4444;
    private static final String QUIT_MESSAGE = "quit";
    private static final Gson GSON = new Gson();

    public static void main() {

        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            Thread.currentThread().setName("Player client thread " + socket.getLocalPort());

            Message message = GSON.fromJson(reader.readLine(), Message.class);
            System.out.println(message.description());

            if (message.status() != MessageStatus.CLIENT_REJECTED) {
                while (true) {
                    System.out.print("Enter command: ");
                    String command = scanner.nextLine();

                    if (QUIT_MESSAGE.equals(command)) {
                        writer.println("Player " + socket.getLocalPort() + " has exited the game.");
                        break;
                    }

                    sendCommand(command, writer);
                    readReply(reader);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    private static void sendCommand(String command, PrintWriter writer) {
        System.out.println("Sending command <" + command + "> to the server...");
        writer.println(command);
    }

    private static void readReply(BufferedReader reader) throws IOException {
        String reply = reader.readLine();
        System.out.println("The server replied <" + reply + ">");
    }
}