package bg.sofia.uni.fmi.mjt.dungeonsonline.common;

import java.net.InetAddress;

public class Request {

    private final String command;
    private final InetAddress address;

    public Request(String command, InetAddress address) {
        this.command = command;
        this.address = address;
    }

    public String getCommand() {
        return command;
    }

    public InetAddress getAddress() {
        return address;
    }
}
