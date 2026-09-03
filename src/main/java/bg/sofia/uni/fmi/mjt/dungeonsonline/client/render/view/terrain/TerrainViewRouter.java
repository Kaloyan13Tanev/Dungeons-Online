package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.terrain;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.TerrainKind;

import java.util.EnumMap;
import java.util.Map;

public class TerrainViewRouter {

    private final Map<TerrainKind, TerrainView> views = new EnumMap<>(TerrainKind.class);

    public void register(TerrainKind kind, TerrainView view) {
        views.put(kind, view);
    }

    public TerrainView route(TerrainKind kind) {
        TerrainView view = views.get(kind);

        if (view == null) {
            throw new IllegalStateException("No view registered for " + kind);
        }

        return view;
    }

}
