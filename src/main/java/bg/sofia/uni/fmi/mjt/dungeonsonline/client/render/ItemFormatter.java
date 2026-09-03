package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.actor.ActorViewRouter;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.item.ItemViewRouter;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ItemDTO;

public class ItemFormatter {

    private static final String EMPTY_SLOT = "[EMPTY]";

    private final ItemViewRouter views;
    private final ActorViewRouter actors;
    private final String emptySlot;

    public ItemFormatter(ItemViewRouter views, ActorViewRouter actors) {
        this(views, actors, EMPTY_SLOT);
    }

    ItemFormatter(ItemViewRouter views, ActorViewRouter actors, String emptySlot) {
        this.views = views;
        this.actors = actors;
        this.emptySlot = emptySlot;
    }

    public String format(ItemDTO item) {
        if (item == null) {
            return emptySlot;
        }

        return "[" + views.route(item) + "]";
    }

    public char symbol(ItemDTO item) {
        return views.route(item).symbol();
    }

    public String format(ActorDTO actor) {
        return "[" + actors.route(actor) + "]";
    }

}
