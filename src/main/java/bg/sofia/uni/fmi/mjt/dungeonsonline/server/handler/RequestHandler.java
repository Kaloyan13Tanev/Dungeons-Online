package bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngine;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.TargetNotReachableException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.EmptySlotException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.NotEnoughManaException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.backpack.FullBackpackException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.item.ItemLevelTooHighException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.InvalidMoveException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.RequestMapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Direction;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.DropRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.GiveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.MoveRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.PickUpRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.QuitRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.Request;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.SelectRequest;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.request.UseRequest;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler {

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    private static final String UNREADABLE_REQUEST = "That command could not be read by the server.";
    private static final String FAILED_REQUEST = "Something went wrong while handling that command.";

    private final ConnectionRegistry registry;
    private final GameEngine engine;
    private final RequestMapper mapper = new RequestMapper();

    public RequestHandler(ConnectionRegistry registry, GameEngine engine) {
        this.registry = registry;
        this.engine = engine;
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
        } catch (InvalidMoveException | TargetNotReachableException | EmptySlotException
                 | FullBackpackException | ItemLevelTooHighException | NotEnoughManaException e) {
            registry.sendTo(playerId, e.getMessage());
            LOGGER.log(Level.WARNING, "Player {0} tried {1} and was refused: {2}",
                new Object[] {playerId, request, e.getMessage()});
        } catch (RuntimeException e) {
            registry.sendTo(playerId, FAILED_REQUEST);
            LOGGER.log(Level.SEVERE, "Failed to handle " + request + " from player " + playerId + ".", e);
        }
    }

    private void route(int playerId, Request request) {
        switch (request) {
            case MoveRequest(Direction direction) -> {
                engine.move(playerId, direction);
                registry.sendToAll("Player " + playerId + " moved " + direction);
            }
            case QuitRequest ignored -> registry.unregister(playerId);
            case SelectRequest(int slot) -> engine.select(playerId, slot);
            case UseRequest(Integer targetId) -> engine.use(playerId, targetId);
            case PickUpRequest(int treasureId) -> engine.pickUp(playerId, treasureId);
            case GiveRequest(int targetPlayerId) -> engine.give(playerId, targetPlayerId);
            case DropRequest ignored -> engine.drop(playerId);
        }
    }
}
