package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render.view.terrain;

import java.util.ArrayList;
import java.util.List;

public record ObstacleView() implements TerrainView {

    private static final char SYMBOL = 'X';
    private static final char GAP = ' ';

    @Override
    public List<String> lines(int width, int height) {
        List<String> lines = new ArrayList<>(height);

        for (int line = 0; line < height; line++) {
            StringBuilder cells = new StringBuilder(width);

            for (int cell = 0; cell < width; cell++) {
                cells.append(line % 2 == cell % 2 ? SYMBOL : GAP);
            }

            lines.add(cells.toString());
        }

        return List.copyOf(lines);
    }

}
