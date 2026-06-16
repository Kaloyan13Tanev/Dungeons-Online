package bg.sofia.uni.fmi.mjt.dungeonsonline.client.channel;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.serialization.RequestSerializer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.QuitRequest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

public class ClientSendChannel {

    private final BufferedWriter writer;
    private final Scanner scanner;
    private final RequestSerializer serializer = new RequestSerializer();

    public ClientSendChannel(BufferedWriter writer, Scanner scanner) {
        this.writer = writer;
        this.scanner = scanner;
    }

    public void start() throws IOException {
        while (true) {
            System.out.print("Enter message: ");
            String message = scanner.nextLine();
            if ("quit".equals(message)) {
                writer.write(serializer.serialize(new QuitRequest()));
                writer.flush();
                break;
            } else if (message.equalsIgnoreCase("move")) {
                writer.write(serializer.serialize(new MoveRequest()));
            }

            writer.flush();
        }
    }
}