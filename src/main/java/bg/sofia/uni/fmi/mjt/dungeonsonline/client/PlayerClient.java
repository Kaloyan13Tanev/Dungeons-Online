package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.channel.ClientReceiveChannel;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.channel.ClientSendChannel;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.serialization.ResponseDeserializer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class PlayerClient {

    private static final int SERVER_PORT = 4444;

    static void main() {

        System.out.println("Connected to the server");
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            ResponseDeserializer deserializer = new ResponseDeserializer(reader);
            Response response = deserializer.deserialize();

            if (response.getMessage().equals("Server is full. Try again later.")) {
                System.out.println(response.getMessage());
            } else if (response.getMessage().equals("Accepted into the game")) {
                startReceiveChannel(reader);
                startSendChannel(writer, scanner);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void startReceiveChannel(BufferedReader reader) {
        Thread receiveChannel = new Thread(new ClientReceiveChannel(reader));
        receiveChannel.setDaemon(true);
        receiveChannel.start();
    }

    private static void startSendChannel(BufferedWriter writer, Scanner scanner) throws IOException {
        ClientSendChannel sendChannel = new ClientSendChannel(writer, scanner);
        sendChannel.start();
    }
}
