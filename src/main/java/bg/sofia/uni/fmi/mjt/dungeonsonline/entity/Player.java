package bg.sofia.uni.fmi.mjt.dungeonsonline.entity;

import bg.sofia.uni.fmi.mjt.dungeonsonline.attribute.Backpack;
import bg.sofia.uni.fmi.mjt.dungeonsonline.attribute.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.exception.OutOfGameMapException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.exception.PlayerLimitReachedException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.exception.TileNotWalkableException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.map.WalkableTile;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderTile;

import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class Player extends Entity {

    private static final int PLAYER_LIMIT = 9;

    private static final int EXIT_KEY = 27;

    private static int itPlayerID = 0;

    private final int playerID;
    private final Backpack backpack = new Backpack();

    private int x = 0;
    private int y = 0;
    private int selectedItem;

    public Player() {
        itPlayerID += 1;

        if (itPlayerID > PLAYER_LIMIT) {
            throw new PlayerLimitReachedException("At most " + PLAYER_LIMIT + " players can be in the game!");
        }

        playerID = itPlayerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public void takeDamage() {

    }

    @Override
    public void die() {

    }

    public void play(GameMap gameMap) throws IOException {

        terminal.enterRawMode();

        boolean loop = true;
        while (loop) {
            int key = terminal.reader().read(1);

            switch (key) {
                case EXIT_KEY -> loop = false;
                case 'w', 'W' -> move(Direction.UP, gameMap);
                case 'a', 'A' -> move(Direction.LEFT, gameMap);
                case 's', 'S' -> move(Direction.DOWN, gameMap);
                case 'd', 'D' -> move(Direction.RIGHT, gameMap);
            }
        }
    }

    private void move(Direction dir, GameMap gameMap) {
        int newRow = x + dir.getRow();
        int newCol = y + dir.getCol();

        if (!isValidPos(newRow, newCol, gameMap)) {
            throw new OutOfGameMapException("You cannot move in this direction! The map ends here.");
        }

        if (!gameMap.getTile(newRow, newCol).isWalkable()) {
            throw new TileNotWalkableException("You cannot step on this tile!");
        }

        WalkableTile currTile = (WalkableTile) gameMap.getTile(x, y);
        WalkableTile newTile = (WalkableTile) gameMap.getTile(newRow, newCol);

        currTile.removePlayer(this);
        RenderTile.render(currTile, this);

        x = newRow;
        y = newCol;

        newTile.addPlayer(this);
        RenderTile.render(newTile, this);

    }

    private boolean isValidPos(int xPos, int yPos, GameMap gameMap) {
        return xPos >= 0 && yPos >= 0 && xPos < gameMap.getHeight() && y < gameMap.getWidth();
    }

}
