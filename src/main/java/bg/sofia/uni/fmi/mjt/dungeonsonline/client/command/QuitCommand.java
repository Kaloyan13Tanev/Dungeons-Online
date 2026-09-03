package bg.sofia.uni.fmi.mjt.dungeonsonline.client.command;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.QuitRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.util.Optional;

public record QuitCommand(ClientState state) implements ClientCommand {

    @Override
    public Optional<Request> execute() {
        state.stopPlaying();

        return Optional.of(new QuitRequest());
    }

}
