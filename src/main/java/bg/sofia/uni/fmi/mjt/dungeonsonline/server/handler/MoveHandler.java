package bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;

public class MoveHandler implements Handler<MoveRequest> {

    @Override
    public Response handle(int id, MoveRequest request) {
        return new Response("Player " + id + " moved");
    }

}
