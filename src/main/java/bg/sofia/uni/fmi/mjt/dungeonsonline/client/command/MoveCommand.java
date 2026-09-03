package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.util.Optional;

public record MoveCommand(Direction direction) implements ClientCommand {

    @Override
    public Optional<Request> execute() {
        return Optional.of(new MoveRequest(direction));
    }

}
