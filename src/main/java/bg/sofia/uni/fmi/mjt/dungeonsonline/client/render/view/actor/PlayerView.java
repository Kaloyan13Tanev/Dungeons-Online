package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.actor;

public record PlayerView(int id) implements ActorView {

    private static final String NAME = "Player ";

    @Override
    public String symbol() {
        return String.valueOf(id);
    }

    @Override
    public String toString() {
        return NAME + id;
    }

}
