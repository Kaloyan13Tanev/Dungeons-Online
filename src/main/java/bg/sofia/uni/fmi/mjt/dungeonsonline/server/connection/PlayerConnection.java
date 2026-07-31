package bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class PlayerConnection implements AutoCloseable {

    private final int playerId;
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final AtomicBoolean open;

    public PlayerConnection(int playerId, Socket socket) throws IOException {
        this.playerId = playerId;
        this.socket = socket;
        this.reader = new BufferedReader(
            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.open = new AtomicBoolean(true);
    }

    public int playerId() {
        return playerId;
    }

    public boolean isOpen() {
        return open.get();
    }

    public synchronized void send(String message) {
        if (!open.get()) {
            return;
        }

        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            close();
        }
    }

    public void listen(Consumer<String> onRequest) {
        try {
            String request;
            while ((request = reader.readLine()) != null) {
                onRequest.accept(request);
            }
        } catch (IOException e) {
            if (open.get()) {
            }
        }
    }

    @Override
    public void close() {
        if (!open.compareAndSet(true, false)) {
            return;
        }

        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
