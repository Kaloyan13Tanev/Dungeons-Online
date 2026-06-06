package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

public class RequestRouter {

    public void route(String request) {
        switch (request) {
            case "quit" -> System.out.println("Player quits");
            default -> System.out.println("Command " + request + " was called");
        }
    }

}
