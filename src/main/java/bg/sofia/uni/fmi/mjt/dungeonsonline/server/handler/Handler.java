package bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.common.response.Response;

public interface Handler {

    public Response handle(int id, Request request);

}
