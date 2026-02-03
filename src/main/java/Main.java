import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderBackpack;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderMessages;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderStats;
import bg.sofia.uni.fmi.mjt.dungeonsonline.temp.TempMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.temp.TempPlayer;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderMap;

import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class Main {

    public static void main(String[] args) throws IOException {
        terminal.writer().println(terminal.getWidth());
        terminal.writer().println(terminal.getHeight());
        terminal.flush();
        RenderMap renderMap = new RenderMap();
        renderMap.render();
        RenderStats renderStats = new RenderStats();
        renderStats.render(TempPlayer.THE_PLAYER);
        RenderBackpack renderBackpack = new RenderBackpack();
        renderBackpack.renderBackpack(TempPlayer.THE_PLAYER.getBackpack());
        RenderMessages renderMessages = new RenderMessages();
        renderMessages.render();
        TempPlayer.THE_PLAYER.play(TempMap.GAME_MAP);
    }

}
