package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.SelectRequest;

import java.util.Optional;

public record SelectSlotCommand(int slot) implements ClientCommand {

    @Override
    public Optional<Request> execute() {
        return Optional.of(new SelectRequest(slot));
    }

}
