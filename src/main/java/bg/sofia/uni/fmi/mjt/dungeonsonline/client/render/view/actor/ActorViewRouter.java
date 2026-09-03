package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.actor;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class ActorViewRouter {

    private final Map<ActorKind, Function<ActorDTO, ActorView>> views = new EnumMap<>(ActorKind.class);

    public void register(ActorKind kind, Function<ActorDTO, ActorView> view) {
        views.put(kind, view);
    }

    public ActorView route(ActorDTO actor) {
        Function<ActorDTO, ActorView> view = views.get(actor.kind());

        if (view == null) {
            throw new IllegalStateException("No view registered for " + actor.kind());
        }

        return view.apply(actor);
    }

}
