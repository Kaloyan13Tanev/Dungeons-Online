package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.UseRequest;

import java.util.Optional;

public class UseCommand implements ClientCommand {

    private final Selection selection;
    private final GameRenderer renderer;

    public UseCommand(Selection selection, GameRenderer renderer) {
        this.selection = selection;
        this.renderer = renderer;
    }

    @Override
    public Optional<Request> execute() {
        if (!selection.open(Mode.CHOOSING_TARGET)) {
            return Optional.of(new UseRequest(null));
        }

        renderer.renderSelection();

        return Optional.empty();
    }

}
