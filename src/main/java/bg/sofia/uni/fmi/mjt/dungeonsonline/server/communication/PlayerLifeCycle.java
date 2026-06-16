package bg.sofia.uni.fmi.mjt.dungeonsonline.server.communication;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.player.PlayerIdPool;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.registry.ClientConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.router.Router;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.serialization.RequestDeserializer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.serialization.ResponseSerializer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PlayerLifeCycle implements Runnable {

    private static final Response ACCEPT_RESPONSE = new Response("Accepted into the game");
    private static final String QUIT_MESSAGE = "A player has quit | id: ";

    private final Socket socket;
    private final Router router;
    private final ClientConnectionRegistry registry;
    private final PlayerIdPool idPool;
    private final ResponseSerializer serializer;
    private final RequestDeserializer deserializer;
    private final int id;

    public PlayerLifeCycle(Socket socket, Router router, ClientConnectionRegistry registry, PlayerIdPool idPool,
                           ResponseSerializer serializer, RequestDeserializer deserializer, int id) {
        this.socket = socket;
        this.router = router;
        this.registry = registry;
        this.idPool = idPool;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.id = id;
    }

    @Override
    public void run() {
        try (socket;
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            registry.acceptNewConnection(id, writer);

            writer.write(serializer.serialize(ACCEPT_RESPONSE));
            writer.flush();

            ClientRequestHandler clientRequestHandler = new ClientRequestHandler(socket.getInputStream(), id, router,
                    registry, deserializer);
            clientRequestHandler.listen();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            clearUp();
        }
    }

    private void clearUp() {
        registry.removeConnection(id);
        registry.sendResponse(new Response(QUIT_MESSAGE + id));
        idPool.release(id);
    }
}
