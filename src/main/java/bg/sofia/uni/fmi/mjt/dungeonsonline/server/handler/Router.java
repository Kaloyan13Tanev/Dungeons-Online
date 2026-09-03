package bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEvent;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.RequestType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Router {

    private final Map<RequestType, BiFunction<Integer, Request, List<GameEvent>>> routes =
        new EnumMap<>(RequestType.class);

    public <R extends Request> void register(RequestType type, Class<R> requestClass,
                                             BiFunction<Integer, R, List<GameEvent>> action) {
        routes.put(type, (playerId, request) -> action.apply(playerId, requestClass.cast(request)));
    }

    public List<GameEvent> route(int playerId, Request request) {
        RequestType type = RequestType.of(request);
        BiFunction<Integer, Request, List<GameEvent>> action = routes.get(type);

        if (action == null) {
            throw new IllegalStateException("No route registered for " + type);
        }

        return action.apply(playerId, request);
    }

}
