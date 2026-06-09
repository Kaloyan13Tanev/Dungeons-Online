package bg.sofia.uni.fmi.mjt.dungeonsonline.common;

public class Request {

    private final String command;
    private final int port;

    public Request(String command, int port) {
        this.command = command;
        this.port = port;
    }

    public String getCommand() {
        return command;
    }

    public int getPort() {
        return port;
    }
}
