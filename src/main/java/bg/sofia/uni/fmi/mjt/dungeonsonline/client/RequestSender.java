package bg.sofia.uni.fmi.mjt.dungeonsonline.client;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.RequestMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestSender {

    private static final Logger LOGGER = Logger.getLogger(RequestSender.class.getName());

    private final BufferedWriter writer;
    private final RequestMapper mapper = new RequestMapper();

    public RequestSender(BufferedWriter writer) {
        this.writer = writer;
    }

    public synchronized void send(Request request) {
        try {
            writer.write(mapper.serialize(request));
            writer.newLine();
            writer.flush();

            LOGGER.log(Level.FINE, "Sent {0} to the server.", request);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to send " + request + " to the server.", e);
        }
    }

}
