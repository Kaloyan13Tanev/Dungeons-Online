package bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;

public class MoveHandler implements Handler {
    @Override
    public Response handle(int id, Request request) {
        return new Response("Player " + id + " moved");
    }
}
