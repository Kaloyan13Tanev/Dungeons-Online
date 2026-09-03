package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.terrain;

import java.util.Collections;
import java.util.List;

public record GroundView() implements TerrainView {

    private static final char SYMBOL = ' ';

    @Override
    public List<String> lines(int width, int height) {
        return Collections.nCopies(height, String.valueOf(SYMBOL).repeat(width));
    }

}
