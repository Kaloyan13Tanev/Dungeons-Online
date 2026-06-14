package bg.sofia.uni.fmi.mjt.dungeonsonline.server.registry;

import org.example.common.response.Response;
import org.example.server.serialization.ResponseSerializer;

import java.io.BufferedWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientConnectionRegistry {

    private final Map<Integer, BufferedWriter> registry;
    private final ResponseSerializer serializer;

    public ClientConnectionRegistry(Map<Integer, BufferedWriter> registry, ResponseSerializer serializer) {
        this.registry = new ConcurrentHashMap<>(registry);
        this.serializer = serializer;
    }

    public void acceptNewConnection(int id, BufferedWriter writer) {
        if (registry.putIfAbsent(id, writer) != null) {
            throw new IllegalStateException("Connection with id " + id + " already exists in the registry");
        }
    }

    public void removeConnection(int id) {
        if (!registry.containsKey(id)) {
            throw new IllegalArgumentException("No such id in registry");
        }

        registry.remove(id);
    }

    public void sendResponse(Response response) {
        String message = serializer.serialize(response);

        for (BufferedWriter writer : registry.values()) {
            Thread.ofVirtual().start(new Sender(message, writer));
        }
    }

}