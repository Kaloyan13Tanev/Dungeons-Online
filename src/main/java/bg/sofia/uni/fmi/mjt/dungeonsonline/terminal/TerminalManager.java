package bg.sofia.uni.fmi.mjt.dungeonsonline.terminal;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class TerminalManager {

    public static Terminal terminal;

    static {
        try {
            terminal = TerminalBuilder
                    .builder()
                    .system(true)
                    .build();
            terminal.enterRawMode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
