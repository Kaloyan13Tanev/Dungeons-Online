package bg.sofia.uni.fmi.mjt.dungeonsonline.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServer {

    private static final int SERVER_PORT = 4444;
    private static final int MAX_PLAYER_COUNT = 3;

    private static AtomicInteger currPlayerCount = new AtomicInteger(0);

    static void main() {
        Thread.currentThread().setName("Echo Server Thread");

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
             ExecutorService executor = Executors.newFixedThreadPool(MAX_PLAYER_COUNT)) {

            InetAddress serverAddress = InetAddress.getLocalHost();
            System.out.println("Server started on " + serverAddress.getHostAddress() +
                    " and listening for connection requests on port " + SERVER_PORT);

            Socket clientSocket;

            while (true) {

                clientSocket = serverSocket.accept();

                System.out.println("Accepted connection request from client " + clientSocket.getInetAddress() + ":" +
                        clientSocket.getPort());

                if (currPlayerCount.get() < MAX_PLAYER_COUNT) {
                    playerJoinsLobby(clientSocket, executor);
                } else {
                    fullLobbyRejection(clientSocket);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }

    private static void playerJoinsLobby(Socket clientSocket, ExecutorService executor) {
        currPlayerCount.incrementAndGet();
        ClientRequestHandler clientHandler = new ClientRequestHandler(clientSocket, currPlayerCount);
        executor.execute(clientHandler);
        System.out.println("Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() +
                " successfully joined lobby.");
    }

    private static void fullLobbyRejection(Socket clientSocket) {
        FullLobbyHandler fullLobbyHandler = new FullLobbyHandler(clientSocket);
        Thread.ofVirtual().start(fullLobbyHandler);
        System.out.println("Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() +
                " was forced to exit due to full lobby.");
    }

}