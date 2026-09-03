package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.input.InputLoop;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerClient {

    private static final Logger LOGGER = Logger.getLogger(PlayerClient.class.getName());

    private final Console console;
    private final BufferedReader reader;
    private final GameRenderer renderer;
    private final InputLoop inputLoop;

    public PlayerClient(Console console, BufferedReader reader, GameRenderer renderer, InputLoop inputLoop) {
        this.console = console;
        this.reader = reader;
        this.renderer = renderer;
        this.inputLoop = inputLoop;
    }

    public void run() throws IOException {
        ServerListener listener = new ServerListener(reader, renderer);
        Thread listenerThread = Thread.ofVirtual().start(listener);

        try {
            inputLoop.run();
        } finally {
            listener.stop();
            listenerThread.interrupt();

            console.clearScreen();
            console.close();

            LOGGER.log(Level.CONFIG, "The session ended.");
        }
    }

}
