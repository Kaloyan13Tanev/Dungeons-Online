import bg.sofia.uni.fmi.mjt.dungeonsonline.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.temp.TempMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.temp.TempPlayer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderMap;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        RenderMap renderMap = new RenderMap();
        renderMap.render();
        TempPlayer.THE_PLAYER.play(TempMap.GAME_MAP);
    }

}
