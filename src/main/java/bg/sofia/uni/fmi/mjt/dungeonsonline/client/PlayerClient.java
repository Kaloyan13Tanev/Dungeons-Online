package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

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

            //TODO: inject the channels
            Thread receiveChannel = new Thread(new ClientReceiveChannel(reader));
            receiveChannel.start();

            ClientSendChannel sendChannel = new ClientSendChannel(writer, scanner);
            sendChannel.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
