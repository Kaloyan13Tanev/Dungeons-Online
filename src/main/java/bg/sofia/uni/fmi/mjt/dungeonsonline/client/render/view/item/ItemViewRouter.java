package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.item;

import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ItemKind;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class ItemViewRouter {

    private final Map<ItemKind, Function<ItemDTO, ItemView>> views = new EnumMap<>(ItemKind.class);

    public void register(ItemKind kind, Function<ItemDTO, ItemView> view) {
        views.put(kind, view);
    }

    public ItemView route(ItemDTO item) {
        Function<ItemDTO, ItemView> view = views.get(item.kind());

        if (view == null) {
            throw new IllegalStateException("No view registered for " + item.kind());
        }

        return view.apply(item);
    }

}
