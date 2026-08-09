package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Message;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;

import java.util.List;

public class MessageRenderer implements Renderer {

    private static final int START_ROW = 17;
    private static final int LINES = 7;
    private static final String LABEL = "=".repeat(31) + " MESSAGES " + "=".repeat(31);

    private static final String ERROR_ON = "\033[31m";
    private static final String COLOR_OFF = "\033[0m";

    private final Console console;
    private final int startColumn;

    public MessageRenderer(Console console, int startColumn) {
        this.console = console;
        this.startColumn = startColumn;
    }

    @Override
    public void render(ClientState state) {
        List<Message> messages = state.getMessages();

        console.clearArea(START_ROW, startColumn, LABEL.length(), LINES);

        console.moveCursor(START_ROW, startColumn);
        console.print(LABEL);

        for (int line = 0; line < messages.size(); line++) {
            Message message = messages.get(line);

            console.moveCursor(START_ROW + line + 1, startColumn);
            console.print(message.error() ? ERROR_ON + message.text() + COLOR_OFF : message.text());
        }

        console.flush();
    }

}
