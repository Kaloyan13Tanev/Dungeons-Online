package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.util.Optional;

public class CancelCommand implements ClientCommand {

    private final Selection selection;
    private final GameRenderer renderer;

    public CancelCommand(Selection selection, GameRenderer renderer) {
        this.selection = selection;
        this.renderer = renderer;
    }

    @Override
    public Optional<Request> execute() {
        selection.close();
        renderer.renderSelection();

        return Optional.empty();
    }

}
