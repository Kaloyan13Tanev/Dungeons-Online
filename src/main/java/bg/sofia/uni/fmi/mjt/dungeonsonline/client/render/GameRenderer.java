package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.HandshakeResponse;

public interface GameRenderer {

    void renderHandshake(HandshakeResponse response);

    void renderState(GameStateDTO state);

    void renderEvent(String message);

    void renderError(String message);

    void renderSelection();

}
