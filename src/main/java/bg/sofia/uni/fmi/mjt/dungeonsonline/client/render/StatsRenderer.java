package bg.sofia.uni.fmi.mjt.dungeonsonline.client.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.client.ClientState;
import bg.sofia.uni.fmi.mjt.dungeonsonline.client.console.Console;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.PlayerStateDTO;

public class StatsRenderer implements Renderer {

    private static final int START_ROW = 1;
    private static final int LINES = 3;
    private static final String LABEL = "=".repeat(30) + " YOUR STATS " + "=".repeat(30);

    private static final String LEVEL_FORMAT = "%-18s%s";
    private static final String STATS_FORMAT = "%-18s%-18s%-20s%s";

    private final Console console;
    private final int startColumn;

    public StatsRenderer(Console console, int startColumn) {
        this.console = console;
        this.startColumn = startColumn;
    }

    @Override
    public void render(ClientState state) {
        if (state.getState() == null) {
            return;
        }

        PlayerStateDTO player = state.getState().player();

        console.clearArea(START_ROW, startColumn, LABEL.length(), LINES);

        console.moveCursor(START_ROW, startColumn);
        console.print(LABEL);

        console.moveCursor(START_ROW + 1, startColumn);
        console.print(String.format(LEVEL_FORMAT,
            "Level: " + player.level(),
            "XP: " + player.xp() + "/" + player.xpCap()));

        console.moveCursor(START_ROW + 2, startColumn);
        console.print(String.format(STATS_FORMAT,
            "Attack: " + player.attack(),
            "Defense: " + player.defense(),
            "Health: " + player.health() + "/" + player.maxHealth(),
            "Mana: " + player.mana() + "/" + player.maxMana()));

        console.flush();
    }

}
