package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.util.Optional;

public class OpenListCommand implements ClientCommand {

    private static final String NOTHING_TO_CHOOSE = "There is nothing to choose here!";

    private final Selection selection;
    private final GameRenderer renderer;
    private final Mode mode;

    public OpenListCommand(Selection selection, GameRenderer renderer, Mode mode) {
        this.selection = selection;
        this.renderer = renderer;
        this.mode = mode;
    }

    @Override
    public Optional<Request> execute() {
        if (selection.open(mode)) {
            renderer.renderSelection();
        } else {
            renderer.renderError(NOTHING_TO_CHOOSE);
        }

        return Optional.empty();
    }

}
