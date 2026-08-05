package bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.RequestMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.AttackRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.CastRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.DropRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.GiveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.PickUpRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.QuitRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler {

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    private static final String UNREADABLE_REQUEST = "That command could not be read by the server.";
    private static final String FAILED_REQUEST = "Something went wrong while handling that command.";

    private final ConnectionRegistry registry;
    private final RequestMapper mapper = new RequestMapper();

    public RequestHandler(ConnectionRegistry registry) {
        this.registry = registry;
    }

    public void handle(int playerId, String message) {
        Request request;
        try {
            request = mapper.deserialize(message);
        } catch (InvalidRequestException e) {
            registry.sendTo(playerId, UNREADABLE_REQUEST);
            LOGGER.log(Level.WARNING, "Failed to deserialize a request from player " + playerId + ".", e);
            return;
        }

        LOGGER.log(Level.INFO, "Player {0} sent {1}.", new Object[] {playerId, request});
        try {
            route(playerId, request);
        } catch (RuntimeException e) {
            registry.sendTo(playerId, FAILED_REQUEST);
            LOGGER.log(Level.SEVERE, "Failed to handle " + request + " from player " + playerId + ".", e);
        }
    }

    private void route(int playerId, Request request) {
        switch (request) {
            case MoveRequest(Direction direction) ->
                registry.sendToAll("Player " + playerId + " moved " + direction);
            case QuitRequest ignored -> registry.unregister(playerId);
            case PickUpRequest ignored -> registry.sendTo(playerId, "Picking up is not implemented yet.");
            case GiveRequest ignored -> registry.sendTo(playerId, "Giving items is not implemented yet.");
            case AttackRequest ignored -> registry.sendTo(playerId, "Attacking is not implemented yet.");
            case CastRequest ignored -> registry.sendTo(playerId, "Casting is not implemented yet.");
            case DropRequest ignored -> registry.sendTo(playerId, "Dropping is not implemented yet.");
        }
    }
}
