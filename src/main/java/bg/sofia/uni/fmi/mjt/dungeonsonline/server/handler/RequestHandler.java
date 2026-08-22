package bg.sofia.uni.fmi.mjt.dungeonsonline.server.handler;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.connection.ConnectionRegistry;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEngine;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.GameEvent;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.InvalidActionException;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
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
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.ErrorResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.EventResponse;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response.StateResponse;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler {

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    private static final String UNREADABLE_REQUEST = "That command could not be read by the server.";
    private static final String FAILED_REQUEST = "Something went wrong while handling that command.";

    private final ConnectionRegistry registry;
    private final GameEngine engine;
    private final RequestMapper mapper;

    public RequestHandler(ConnectionRegistry registry, GameEngine engine) {
        this(registry, engine, new RequestMapper());
    }

    public RequestHandler(ConnectionRegistry registry, GameEngine engine, RequestMapper mapper) {
        this.registry = registry;
        this.engine = engine;
        this.mapper = mapper;
    }

    public void handle(int playerId, String message) {
        Request request;
        try {
            request = mapper.deserialize(message);
        } catch (InvalidRequestException e) {
            registry.sendTo(playerId, new ErrorResponse(UNREADABLE_REQUEST));
            LOGGER.log(Level.WARNING, "Failed to deserialize a request from player " + playerId + ".", e);
            return;
        }

        LOGGER.log(Level.INFO, "Player {0} sent {1}.", new Object[] {playerId, request});
        try {
            distribute(route(playerId, request));
        } catch (InvalidActionException e) {
            registry.sendTo(playerId, new ErrorResponse(e.getMessage()));
            LOGGER.log(Level.WARNING, "Player {0} tried {1} and was refused: {2}",
                new Object[] {playerId, request, e.getMessage()});
        } catch (RuntimeException e) {
            registry.sendTo(playerId, new ErrorResponse(FAILED_REQUEST));
            LOGGER.log(Level.SEVERE, "Failed to handle " + request + " from player " + playerId + ".", e);
        }
    }

    public void distribute(List<GameEvent> events) {
        for (GameEvent event : events) {
            registry.sendTo(event.recipients(), new EventResponse(event.message()));
        }

        for (Map.Entry<Integer, GameStateDTO> state : engine.stateForAll().entrySet()) {
            registry.sendTo(state.getKey(), new StateResponse(state.getValue()));
        }
    }

    private List<GameEvent> route(int playerId, Request request) {
        return switch (request) {
            case MoveRequest(Direction direction) -> engine.move(playerId, direction);
            case SelectRequest(int slot) -> engine.select(playerId, slot);
            case UseRequest(Integer targetId) -> engine.use(playerId, targetId);
            case PickUpRequest(int treasureId) -> engine.pickUp(playerId, treasureId);
            case GiveRequest(int targetPlayerId) -> engine.give(playerId, targetPlayerId);
            case DropRequest ignored -> engine.drop(playerId);
            case QuitRequest ignored -> quit(playerId);
        };
    }

    private List<GameEvent> quit(int playerId) {
        registry.unregister(playerId);

        return List.of();
    }

}
