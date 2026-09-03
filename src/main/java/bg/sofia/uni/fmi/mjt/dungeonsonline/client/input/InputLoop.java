package bg.sofia.uni.fmi.mjt.dungeonsonline.client.input;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.RequestSender;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;

import java.io.IOException;

public class InputLoop {

    private final KeyReader reader;
    private final ClientState state;
    private final Selection selection;
    private final KeyBindings bindings;
    private final RequestSender sender;

    public InputLoop(KeyReader reader, ClientState state, Selection selection, KeyBindings bindings,
                     RequestSender sender) {
        this.reader = reader;
        this.state = state;
        this.selection = selection;
        this.bindings = bindings;
        this.sender = sender;
    }

    public void run() throws IOException {
        reader.enterRawMode();

        while (state.isPlaying()) {
            int key = reader.read();
            if (key == KeyReader.END_OF_INPUT) {
                break;
            }

            bindings.press(selection.mode(), key).ifPresent(sender::send);
        }
    }

}
