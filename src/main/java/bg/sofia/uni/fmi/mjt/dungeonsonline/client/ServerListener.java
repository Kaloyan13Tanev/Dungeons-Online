package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.InvalidResponseException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ResponseMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.ErrorResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.EventResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.HandshakeResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.Response;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.StateResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerListener implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ServerListener.class.getName());

    private final BufferedReader reader;
    private final GameRenderer renderer;
    private final ResponseMapper mapper;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public ServerListener(BufferedReader reader, GameRenderer renderer) {
        this(reader, renderer, new ResponseMapper());
    }

    ServerListener(BufferedReader reader, GameRenderer renderer, ResponseMapper mapper) {
        this.reader = reader;
        this.renderer = renderer;
        this.mapper = mapper;
    }

    @Override
    public void run() {
        try {
            String line;
            while (running.get() && (line = reader.readLine()) != null) {
                handle(line);
            }
        } catch (IOException e) {
            if (running.get()) {
                LOGGER.log(Level.SEVERE, "Lost the connection to the server.", e);
                renderer.renderError("Lost the connection to the server.");
            }
        }

        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }

    public void stop() {
        running.set(false);
    }

    private void handle(String line) {
        Response response;
        try {
            response = mapper.deserialize(line);
        } catch (InvalidResponseException e) {
            LOGGER.log(Level.WARNING, "Could not read a response from the server.", e);
            return;
        }

        switch (response) {
            case HandshakeResponse handshake -> handleHandshake(handshake);
            case StateResponse(var state) -> renderer.renderState(state);
            case EventResponse(String message) -> renderer.renderEvent(message);
            case ErrorResponse(String message) -> renderer.renderError(message);
        }
    }

    private void handleHandshake(HandshakeResponse handshake) {
        if (!handshake.accepted()) {
            LOGGER.log(Level.INFO, "The server refused the connection: {0}", handshake.reason());
            renderer.renderError(handshake.reason());
            running.set(false);
            return;
        }

        renderer.renderHandshake(handshake);
    }

}
