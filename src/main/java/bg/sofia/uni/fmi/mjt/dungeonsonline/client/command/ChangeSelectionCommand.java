package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.util.Optional;

public class ChangeSelectionCommand implements ClientCommand {

    private final Selection selection;
    private final GameRenderer renderer;
    private final int offset;

    public ChangeSelectionCommand(Selection selection, GameRenderer renderer, int offset) {
        this.selection = selection;
        this.renderer = renderer;
        this.offset = offset;
    }

    @Override
    public Optional<Request> execute() {
        selection.step(offset);
        renderer.renderSelection();

        return Optional.empty();
    }

}
