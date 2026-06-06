package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

public class EntryPoint {

    void main() {
        RequestRouter requestRouter = new RequestRouter();
        GameServer gameServer = new GameServer(requestRouter);
        gameServer.run();
    }

}
