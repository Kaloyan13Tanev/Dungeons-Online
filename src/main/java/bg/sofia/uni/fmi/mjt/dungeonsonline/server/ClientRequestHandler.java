package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.Handler;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.registry.ClientConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.router.Router;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.serialization.RequestDeserializer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ClientRequestHandler implements Runnable {

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Integer id;
    private final Router router;
    private final ClientConnectionRegistry registry;

    public ClientRequestHandler(InputStream inputStream, OutputStream outputStream, Integer id, Router router,
                                ClientConnectionRegistry registry) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.id = id;
        this.router = router;
        this.registry = registry;
    }

    @Override
    public void run() {

        RequestDeserializer deserializer = new RequestDeserializer(inputStream);
        try (inputStream;
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

            registry.acceptNewConnection(id, writer);

            Request request;
            while ((request = deserializer.deserialize()) != null) {
                Handler handler = router.route(request);
                Response response = handler.handle(id, request);
                registry.sendResponse(response);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}
