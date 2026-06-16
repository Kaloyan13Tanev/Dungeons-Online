package bg.sofia.uni.fmi.mjt.dungeonsonline.server.communication;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.QuitRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.Handler;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.registry.ClientConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.router.Router;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.serialization.RequestDeserializer;

import java.io.IOException;
import java.io.InputStream;

public class ClientRequestHandler {

    private final InputStream inputStream;
    private final Integer id;
    private final Router router;
    private final ClientConnectionRegistry registry;
    private final RequestDeserializer deserializer;
    public ClientRequestHandler(InputStream inputStream, Integer id, Router router,
                                ClientConnectionRegistry registry, RequestDeserializer deserializer) {
        this.inputStream = inputStream;
        this.id = id;
        this.router = router;
        this.registry = registry;
        this.deserializer = deserializer;
    }

    public void listen() {

        try (inputStream) {

            Request request;
            while ((request = deserializer.deserialize()) != null) {
                if (request.getClass() == QuitRequest.class) {
                    break;
                }

                Handler handler = router.route(request);
                Response response = handler.handle(id, request);
                registry.sendResponse(response);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}
