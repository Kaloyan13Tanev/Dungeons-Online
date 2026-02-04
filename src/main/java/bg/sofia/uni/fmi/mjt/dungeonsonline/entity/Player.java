package bg.sofia.uni.fmi.mjt.dungeonsonline.entity;

import bg.sofia.uni.fmi.mjt.dungeonsonline.attribute.Backpack;
import bg.sofia.uni.fmi.mjt.dungeonsonline.attribute.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.exception.BackpackIsFullException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.exception.ItemLevelTooHighException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.exception.OutOfGameMapException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.exception.PlayerLimitReachedException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.exception.TileNotWalkableException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.map.WalkableTile;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderItemChoice;
import bg.sofia.uni.fmi.mjt.dungeonsonline.render.RenderTile;
import bg.sofia.uni.fmi.mjt.dungeonsonline.selector.ItemSelector;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Item;
import bg.sofia.uni.fmi.mjt.dungeonsonline.treasure.Weapon;

import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.dungeonsonline.terminal.TerminalManager.terminal;

public class Player extends Entity {

    private static final int PLAYER_LIMIT = 9;
    private static final int FINAL_ITEM_INDEX = 9;
    private static final int EXIT_KEY = 27;

    private static int itPlayerID = 0;

    private final int playerID;
    private final Backpack backpack = new Backpack();
    private final Weapon defaultWeapon;

    private int x = 0;
    private int y = 0;
    private Item selectedItem = backpack.get(0);

    public Player(Weapon defaultWeapon) {
        itPlayerID += 1;

        if (itPlayerID > PLAYER_LIMIT) {
            throw new PlayerLimitReachedException("At most " + PLAYER_LIMIT + " players can be in the game!");
        }

        playerID = itPlayerID;

        this.defaultWeapon = defaultWeapon;
    }

    public Player() {
        itPlayerID += 1;

        if (itPlayerID > PLAYER_LIMIT) {
            throw new PlayerLimitReachedException("At most " + PLAYER_LIMIT + " players can be in the game!");
        }

        playerID = itPlayerID;

        defaultWeapon = new Weapon();
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Backpack getBackpack() {
        return backpack;
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
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> setSelectedItem(key);
                case 'e', 'E' -> useItem();
                case 'r', 'R' -> pickUpItem((WalkableTile) gameMap.getTile(x, y));
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

    private void setSelectedItem(int key) {
        if (key == '0') {
            selectedItem = backpack.get(FINAL_ITEM_INDEX);
        } else {
            selectedItem = backpack.get(key - '0' - 1);
        }
    }

    private void useItem() {
        if (selectedItem == null) {
            defaultWeapon.use();
        } else if (level.isLessThan(selectedItem.getLevel())) {
            throw new ItemLevelTooHighException("The level of this item is too high!" +
                    " You will be able to use it when you reach level " + selectedItem.getLevel() + "!");
        } else {
            selectedItem.use();
        }
    }

    private void pickUpItem(WalkableTile tile) throws IOException {
        int index = backpack.findEmptySlot();

        if (index == -1) {
            throw new BackpackIsFullException("Your backpack is full! Drop an item to pick a new one!");
        }

        RenderItemChoice itemChoice = new RenderItemChoice(tile.getItems());
        itemChoice.render();

        ItemSelector selector = new ItemSelector(tile.getItems());
        Item item = selector.initializeSelector();

        if (item != null) {
            tile.removeItem(item);
            RenderTile.render(tile, this);
            backpack.addItem(item);
        }
    }

}
