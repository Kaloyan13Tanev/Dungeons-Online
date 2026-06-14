package bg.sofia.uni.fmi.mjt.dungeonsonline.server.router;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.Handler;

import java.util.Map;

public class Router {

    Map<Class<? extends Request>, ? extends Handler> routes;

    public Router(Map<Class<? extends Request>, ? extends Handler> routes) {
        this.routes = routes;
    }

    public Handler route(Request request) {
        return routes.get(request.getClass());
    }

}
