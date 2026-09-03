package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.DropRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.util.Optional;

public record DropCommand() implements ClientCommand {

    @Override
    public Optional<Request> execute() {
        return Optional.of(new DropRequest());
    }

}
