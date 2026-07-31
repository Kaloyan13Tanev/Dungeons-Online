package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.RequestMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.QuitRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

public class PlayerClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final String USAGE = "Commands: move up|down|left|right, quit";

    private final RequestMapper mapper = new RequestMapper();

    void main() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            System.out.println(USAGE);

            Thread.ofVirtual().start(() -> listen(reader));
            send(writer, scanner);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void listen(BufferedReader reader) {
        try {
            String response;
            while ((response = reader.readLine()) != null) {
                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void send(BufferedWriter writer, Scanner scanner) throws IOException {
        while (scanner.hasNextLine()) {
            Optional<Request> parsed = parse(scanner.nextLine());
            if (parsed.isEmpty()) {
                System.out.println(USAGE);
                continue;
            }

            Request request = parsed.get();
            writer.write(mapper.serialize(request));
            writer.newLine();
            writer.flush();

            if (request instanceof QuitRequest) {
                break;
            }
        }
    }

    private Optional<Request> parse(String input) {
        return switch (input.trim().toLowerCase(Locale.ROOT)) {
            case "move up" -> Optional.of(new MoveRequest(Direction.UP));
            case "move down" -> Optional.of(new MoveRequest(Direction.DOWN));
            case "move left" -> Optional.of(new MoveRequest(Direction.LEFT));
            case "move right" -> Optional.of(new MoveRequest(Direction.RIGHT));
            case "quit" -> Optional.of(new QuitRequest());
            default -> Optional.empty();
        };
    }
}
