package bg.sofia.uni.fmi.mjt.dungeonsonline.client.input;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.Mode;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.command.ClientCommand;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KeyBindings {

    private final Map<Mode, Map<Integer, ClientCommand>> keysPerMode = new EnumMap<>(Mode.class);

    public void bind(Mode mode, int key, ClientCommand command) {
        keysPerMode.computeIfAbsent(mode, ignored -> new HashMap<>()).put(key, command);
    }

    public Optional<Request> press(Mode mode, int key) {
        ClientCommand command = keysPerMode.getOrDefault(mode, Map.of()).get(key);

        if (command == null) {
            return Optional.empty();
        }

        return command.execute();
    }

}
