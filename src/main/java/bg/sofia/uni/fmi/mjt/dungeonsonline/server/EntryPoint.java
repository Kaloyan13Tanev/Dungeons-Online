package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.pool.IdPool;

import java.io.IOException;

public class EntryPoint {

    private static final int PLAYER_COUNT = 9;

    void main() {
        IdPool pool = new IdPool(PLAYER_COUNT);
        ConnectionRegistry registry = new ConnectionRegistry();
        try {
            GameServer server = new GameServer(pool, registry);
            server.run();
        } catch (IOException e) {
            System.out.println("Could not start the server. Is the port already in use?");
        }
    }
}
