package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Minion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.Treasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class GameMap {

    private final TerrainGrid terrain;
    private final Map<Integer, Actor> actors;
    private final Map<Integer, Treasure> treasures;

    public GameMap(TerrainGrid terrain, Map<Integer, Actor> actors, Map<Integer, Treasure> treasures) {
        this.terrain = terrain;
        this.actors = actors;
        this.treasures = treasures;

        requirePlaceable();
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

    public boolean isWalkable(Position position) {
        return terrain.isInside(position) && terrain.isWalkable(position);
    }

    public boolean isFree(Position position) {
        return isWalkable(position) && actorsAt(position).isEmpty();
    }

    public Optional<Position> randomFreePosition(Random random) {
        List<Position> free = new ArrayList<>();

        for (int row = 0; row < terrain.getRows(); row++) {
            for (int col = 0; col < terrain.getCols(); col++) {
                Position position = new Position(row, col);
                if (isFree(position)) {
                    free.add(position);
                }
            }
        }

        if (free.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(free.get(random.nextInt(free.size())));
    }

    private void requirePlaceable() {
        Set<Position> taken = new HashSet<>();

        for (Actor actor : actors.values()) {
            if (!isWalkable(actor.getPosition())) {
                throw new IllegalArgumentException("Cannot place an actor at " + actor.getPosition());
            }

            if (actor instanceof Minion && !taken.add(actor.getPosition())) {
                throw new IllegalArgumentException("There is already a minion at " + actor.getPosition());
            }
        }

        for (Treasure treasure : treasures.values()) {
            if (!isWalkable(treasure.getPosition())) {
                throw new IllegalArgumentException("Cannot place a treasure at " + treasure.getPosition());
            }
        }
    }

}
