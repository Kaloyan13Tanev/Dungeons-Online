package bg.sofia.uni.fmi.mjt.dungeonsonline.server.router;


import bg.sofia.uni.fmi.mjt.dungeonsonline.common.Request;

import java.util.Map;
import java.util.logging.Handler;

public class Router {

    Map<Class<? extends Request>, ? extends Handler> routes;

    public Router(Map<Class<? extends Request>, ? extends Handler> routes) {
        this.routes = routes;
    }

    public Handler route(Request request) {
        return routes.get(request.getClass());
    }

}
