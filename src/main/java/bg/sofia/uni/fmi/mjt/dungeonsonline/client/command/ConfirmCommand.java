package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.GameRenderer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.selection.Selection;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.util.Optional;
import java.util.function.IntFunction;

public class ConfirmCommand implements ClientCommand {

    private final Selection selection;
    private final GameRenderer renderer;
    private final IntFunction<Request> requestFor;

    public ConfirmCommand(Selection selection, GameRenderer renderer, IntFunction<Request> requestFor) {
        this.selection = selection;
        this.renderer = renderer;
        this.requestFor = requestFor;
    }

    @Override
    public Optional<Request> execute() {
        Integer chosen = selection.chosen();
        if (chosen == null) {
            return Optional.empty();
        }

        selection.close();
        renderer.renderSelection();

        return Optional.of(requestFor.apply(chosen));
    }

}
