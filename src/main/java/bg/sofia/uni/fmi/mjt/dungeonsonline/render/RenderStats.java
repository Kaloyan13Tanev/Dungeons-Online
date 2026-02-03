package bg.sofia.uni.fmi.mjt.dungeonsonline.render;

import bg.sofia.uni.fmi.mjt.dungeonsonline.attribute.Level;
import bg.sofia.uni.fmi.mjt.dungeonsonline.attribute.Stats;
import bg.sofia.uni.fmi.mjt.dungeonsonline.entity.Player;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class RenderStats {

    private static final int START_COLUMN = 137;
    private static final int START_ROW = 1;
    private static final String LABEL = "=".repeat(30) + " YOUR STATS " + "=".repeat(30);
    private static final int XP_CAP = 500;

    public void render(Player player) {
        Console.moveCursor(START_ROW, START_COLUMN);
        terminal.writer().print(LABEL);

        int currRow = START_ROW + 1;

        renderLevel(currRow++, player.getLevel());
        renderStats(currRow, player.getStats());
    }

    private void renderLevel(int row, Level level) {
        Console.moveCursor(row, START_COLUMN);

        terminal.writer().print("Level: " + level.getValue() + "\tXP: " + level.getXP() + "/" + XP_CAP);
    }

    private void renderStats(int row, Stats stats) {
        Console.moveCursor(row, START_COLUMN);

        terminal.writer().print("Attack: " + stats.getAttack() + "\tDefense: " + stats.getDefense()
                + "\tHealth: " + stats.getHealth() + "\tMana: " + stats.getMana());

    }

}
