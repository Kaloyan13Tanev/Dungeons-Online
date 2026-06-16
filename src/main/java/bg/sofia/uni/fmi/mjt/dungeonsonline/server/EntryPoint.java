package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.Handler;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.MoveHandler;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.player.PlayerIdPool;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.registry.ClientConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.router.Router;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.serialization.ResponseSerializer;

import java.util.HashMap;
import java.util.Map;

public class EntryPoint {

    void main() {
        PlayerIdPool playerIdPool = new PlayerIdPool();

        Map<Class<? extends Request>, ? extends Handler<? extends Request>> routes = Map.of(
                MoveRequest.class, new MoveHandler()
                // AttackRequest.class, new AttackHandler(),
                // PickUpRequest.class, new PickUpHandler()
        );
        Router router = new Router(routes);

        ResponseSerializer serializer = new ResponseSerializer();
        ClientConnectionRegistry registry = new ClientConnectionRegistry(new HashMap<>(), serializer);

        GameServer gameServer = new GameServer(playerIdPool, router, registry);
        gameServer.run();
    }

}
