package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServer {
    private static final int SERVER_PORT = 4444;
    private static final int MAX_PLAYER_COUNT = 3;

    private static AtomicInteger playerCount = new AtomicInteger(0);
    private final RequestRouter router;

    public GameServer(RequestRouter router) {
        this.router = router;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
             ExecutorService executor = Executors.newFixedThreadPool(MAX_PLAYER_COUNT)) {

            while (true) {
                Socket client = serverSocket.accept();

                if (playerCount.get() < MAX_PLAYER_COUNT) {
                    executor.execute(new ClientRequestReceiver(client.getInputStream(), router));
                } else {
                    //reject player from lobby
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private void clientSetup(InputStream in) {
//        ClientRequestSender clientRequestSender = new ClientRequestSender(in);
//    }

}
