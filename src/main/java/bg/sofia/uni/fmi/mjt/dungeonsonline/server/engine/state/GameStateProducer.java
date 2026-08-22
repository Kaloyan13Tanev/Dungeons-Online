package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStateProducer {

    private final TerrainMapper terrain;
    private final ActorMapper actors;
    private final TreasureMapper treasures;
    private final PlayerStateMapper playerStates;

    public GameStateProducer() {
        this(new ItemMapper());
    }

    GameStateProducer(ItemMapper items) {
        this(new TerrainMapper(), new ActorMapper(), new TreasureMapper(items),
            new PlayerStateMapper(items));
    }

    GameStateProducer(TerrainMapper terrain, ActorMapper actors, TreasureMapper treasures,
                      PlayerStateMapper playerStates) {
        this.terrain = terrain;
        this.actors = actors;
        this.treasures = treasures;
        this.playerStates = playerStates;
    }

    public TerrainDTO terrainOf(GameMap map) {
        return terrain.toDTO(map.getTerrainGrid());
    }

    public Map<Integer, GameStateDTO> stateForAll(GameMap map, Collection<Player> players) {
        List<ActorDTO> actorDTOs = actors.toDTOs(map.getActors());
        List<TreasureDTO> treasureDTOs = treasures.toDTOs(map.getTreasures());

        Map<Integer, GameStateDTO> states = new HashMap<>();
        for (Player player : players) {
            states.put(player.getId(),
                new GameStateDTO(actorDTOs, treasureDTOs, playerStates.toDTO(player)));
        }

        return states;
    }

}
