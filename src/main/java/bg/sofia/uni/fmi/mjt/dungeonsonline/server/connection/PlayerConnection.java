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
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerConnection implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(PlayerConnection.class.getName());

    private final int playerId;
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final AtomicBoolean open;

    public PlayerConnection(int playerId, Socket socket) throws IOException {
        this.playerId = playerId;
        this.socket = socket;
        this.reader = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream()));
        this.open = new AtomicBoolean(true);
    }

    public PlayerConnection(int playerId, Socket socket, BufferedReader reader, BufferedWriter writer) {
        this.playerId = playerId;
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
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
            LOGGER.log(Level.FINE, "Sent to player {0}: {1}", new Object[] {playerId, message});
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to send a message to player " + playerId + ".", e);
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
                LOGGER.log(Level.WARNING, "Lost the connection to player " + playerId + " unexpectedly.", e);
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
            LOGGER.log(Level.FINE, "Socket for player {0} closed.", playerId);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to close socket for player " + playerId, e);
        }
    }

}
