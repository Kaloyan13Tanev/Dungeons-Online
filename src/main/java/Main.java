import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderMap;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class Main {
    public static void main(String[] args) {
        System.out.println(terminal.getWidth());
        System.out.println(terminal.getHeight());
        RenderMap renderMap = new RenderMap();
        renderMap.render();
    }
}
