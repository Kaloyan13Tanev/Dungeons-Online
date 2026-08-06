package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.Treasure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameMap {

    private final TerrainGrid terrain;
    private Map<Integer, Actor> actors;
    private Map<Integer, Treasure> treasures;

    public GameMap(TerrainGrid terrain) {
        this.terrain = terrain;
        this.actors = new ConcurrentHashMap<>();
        this.treasures = new ConcurrentHashMap<>();
    }

    public GameMap(TerrainGrid terrain, Map<Integer, Actor> actors, Map<Integer, Treasure> treasures) {
        this.terrain = terrain;
        this.actors = actors;
        this.treasures = treasures;
    }

    public TerrainGrid getTerrainGrid() {
        return terrain;
    }

    public void addActor(Actor actor) {
        Actor existing = actors.putIfAbsent(actor.getId(), actor);
        if (existing != null) {
            throw new IllegalStateException("Actor " + actor.getId() + " is already on the map");
        }
    }

    public Optional<Actor> removeActor(int actorId) {
        return Optional.ofNullable(actors.remove(actorId));
    }

    public Optional<Actor> getActor(int actorId) {
        return Optional.ofNullable(actors.get(actorId));
    }

    public List<Actor> getActors() {
        return List.copyOf(actors.values());
    }

    public List<Actor> actorsAt(Position position) {
        return actors.values().stream()
            .filter(actor -> actor.getPosition().equals(position))
            .toList();
    }

    public Map<Position, List<Actor>> actorsByPosition() {
        return actors.values().stream()
            .collect(Collectors.groupingBy(Actor::getPosition));
    }

    public void addTreasure(Treasure treasure) {
        Treasure existing = treasures.putIfAbsent(treasure.getId(), treasure);
        if (existing != null) {
            throw new IllegalStateException("Treasure " + treasure.getId() + " is already on the map");
        }
    }

    public Optional<Treasure> removeTreasure(int treasureId) {
        return Optional.ofNullable(treasures.remove(treasureId));
    }

    public Optional<Treasure> getTreasure(int treasureId) {
        return Optional.ofNullable(treasures.get(treasureId));
    }

    public List<Treasure> getTreasures() {
        return List.copyOf(treasures.values());
    }

    public List<Treasure> treasuresAt(Position position) {
        return treasures.values().stream()
            .filter(treasure -> treasure.getPosition().equals(position))
            .toList();
    }

    public Map<Position, List<Treasure>> treasuresByPosition() {
        return treasures.values().stream()
            .collect(Collectors.groupingBy(Treasure::getPosition));
    }

    public Optional<Position> randomFreePosition(Random random) {
        List<Position> free = new ArrayList<>();

        for (int row = 0; row < terrain.getRows(); row++) {
            for (int col = 0; col < terrain.getCols(); col++) {
                Position position = new Position(row, col);
                if (terrain.isWalkable(position) && actorsAt(position).isEmpty()) {
                    free.add(position);
                }
            }
        }

        if (free.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(free.get(random.nextInt(free.size())));
    }
}
