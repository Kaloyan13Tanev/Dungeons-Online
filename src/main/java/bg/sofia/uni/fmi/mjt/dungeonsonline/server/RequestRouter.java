package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import bg.sofia.uni.fmi.mjt.dungeonsonline.common.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.Handler;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler.MoveHandler;

public class RequestRouter {

    private Handler handler;

    public void route(Request request) {
        switch (request.getCommand()) {
            case "quit" -> System.out.println("Player " + request.getAddress() + "quit");
            case "move" -> handler = new MoveHandler();
            default -> System.out.println("Command " + request + " was called by " + request.getAddress());
        }

        handler.handle();
    }

}
