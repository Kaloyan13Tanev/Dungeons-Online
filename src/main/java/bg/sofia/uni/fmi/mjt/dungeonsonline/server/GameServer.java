package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.player.PlayerIdPool;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.registry.ClientConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.router.Router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private static final int SERVER_PORT = 4444;

    private final PlayerIdPool playerIdPool;
    private final Router router;
    private final ClientConnectionRegistry registry;

    public GameServer(PlayerIdPool playerIdPool, Router router, ClientConnectionRegistry registry) {
        this.playerIdPool = playerIdPool;
        this.router = router;
        this.registry = registry;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
             ExecutorService executor = Executors.newFixedThreadPool(playerIdPool.getMaxPlayers())) {

            while (true) {
                Socket client = serverSocket.accept();
                Integer id = playerIdPool.acquireId();

                if (id != null) {
                    executor.execute(new PlayerLifeCycle(client, router, registry, playerIdPool, id));
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
