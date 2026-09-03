package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.actor;

public record MinionView() implements ActorView {

    private static final String SYMBOL = "M";
    private static final String NAME = "Minion";

    @Override
    public String symbol() {
        return SYMBOL;
    }

    @Override
    public String toString() {
        return NAME;
    }

}
