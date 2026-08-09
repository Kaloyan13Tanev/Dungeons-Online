package bg.sofia.uni.fmi.mjt.dungeonsonline.client.console;

import java.io.IOException;

public interface Console {

    void print(String text);

    void moveCursor(int row, int col);

    void clearScreen();

    void clearArea(int startRow, int startCol, int width, int height);

    void flush();

    int read() throws IOException;

    int read(long timeout) throws IOException;

    void enterRawMode();

    void close();

}
