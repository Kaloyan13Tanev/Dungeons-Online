package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map.TerrainGrid;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.GameStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.PlayerStateDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TerrainDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.TreasureDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.TerrainKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameStateProducerTest {

    private static final int FIRST_PLAYER_ID = 1;
    private static final int SECOND_PLAYER_ID = 2;

    private static final int TREASURE_ID = 1;

    private static final int FIRST_LEVEL = 1;
    private static final int SECOND_LEVEL = 2;

    private static final TerrainDTO TERRAIN = new TerrainDTO(List.of(List.of(TerrainKind.GROUND)));

    private static final List<ActorDTO> ACTOR_DTOS =
        List.of(new ActorDTO(FIRST_PLAYER_ID, ActorKind.PLAYER, 0, 0));
    private static final List<TreasureDTO> TREASURE_DTOS =
        List.of(new TreasureDTO(TREASURE_ID, null, 0, 0));

    private static final PlayerStateDTO FIRST_PLAYER_STATE = stateOfLevel(FIRST_LEVEL);
    private static final PlayerStateDTO SECOND_PLAYER_STATE = stateOfLevel(SECOND_LEVEL);

    @Mock
    private TerrainMapper terrainMapper;
    @Mock
    private ActorMapper actorMapper;
    @Mock
    private TreasureMapper treasureMapper;
    @Mock
    private PlayerStateMapper playerStateMapper;

    @Mock
    private GameMap map;
    @Mock
    private TerrainGrid grid;
    @Mock
    private Actor actor;
    @Mock
    private Treasure treasure;
    @Mock
    private Player firstPlayer;
    @Mock
    private Player secondPlayer;

    private GameStateProducer producer;

    @BeforeEach
    void setUp() {
        producer = new GameStateProducer(terrainMapper, actorMapper, treasureMapper, playerStateMapper);
    }

    @Test
    void testTerrainOfReturnsRightTerrainDTO() {
        when(map.getTerrainGrid()).thenReturn(grid);
        when(terrainMapper.toDTO(grid)).thenReturn(TERRAIN);

        assertEquals(TERRAIN, producer.terrainOf(map),
            "GameStateProducer should return the terrain its mapper made of the grid of the map");
    }

    @Test
    void testStateForAllBuildsAStateForEveryPlayer() {
        mockMappedWorld();
        when(firstPlayer.getId()).thenReturn(FIRST_PLAYER_ID);
        when(secondPlayer.getId()).thenReturn(SECOND_PLAYER_ID);

        Map<Integer, GameStateDTO> states = producer.stateForAll(map, List.of(firstPlayer, secondPlayer));

        assertEquals(2, states.size(), "GameStateProducer should build one state per player");
        assertTrue(states.keySet().containsAll(List.of(FIRST_PLAYER_ID, SECOND_PLAYER_ID)),
            "GameStateProducer should key the states by the ids of the players");
    }

    @Test
    void testStateForAllGivesEveryPlayerTheirOwnPlayerState() {
        mockMappedWorld();
        when(firstPlayer.getId()).thenReturn(FIRST_PLAYER_ID);
        when(secondPlayer.getId()).thenReturn(SECOND_PLAYER_ID);
        when(playerStateMapper.toDTO(firstPlayer)).thenReturn(FIRST_PLAYER_STATE);
        when(playerStateMapper.toDTO(secondPlayer)).thenReturn(SECOND_PLAYER_STATE);

        Map<Integer, GameStateDTO> states = producer.stateForAll(map, List.of(firstPlayer, secondPlayer));

        assertEquals(FIRST_PLAYER_STATE, states.get(FIRST_PLAYER_ID).player(),
            "GameStateProducer should give a player the state its mapper made of them");
        assertEquals(SECOND_PLAYER_STATE, states.get(SECOND_PLAYER_ID).player(),
            "GameStateProducer should give a player the state its mapper made of them");
    }

    @Test
    void testStateForAllGivesEveryPlayerTheSameWorld() {
        mockMappedWorld();
        when(firstPlayer.getId()).thenReturn(FIRST_PLAYER_ID);
        when(secondPlayer.getId()).thenReturn(SECOND_PLAYER_ID);

        Map<Integer, GameStateDTO> states = producer.stateForAll(map, List.of(firstPlayer, secondPlayer));

        assertEquals(ACTOR_DTOS, states.get(FIRST_PLAYER_ID).actors(),
            "GameStateProducer should hand out the actors its mapper made of the ones on the map");
        assertEquals(TREASURE_DTOS, states.get(FIRST_PLAYER_ID).treasures(),
            "GameStateProducer should hand out the treasures its mapper made of the ones on the map");
        assertSame(states.get(FIRST_PLAYER_ID).actors(), states.get(SECOND_PLAYER_ID).actors(),
            "GameStateProducer should give every player the same actors");
        assertSame(states.get(FIRST_PLAYER_ID).treasures(), states.get(SECOND_PLAYER_ID).treasures(),
            "GameStateProducer should give every player the same treasures");
    }

    @Test
    void testStateForAllReturnsNoStatesWhenNobodyIsPlaying() {
        mockMappedWorld();

        assertTrue(producer.stateForAll(map, List.of()).isEmpty(),
            "GameStateProducer should return no states when there are no players");
    }

    private void mockMappedWorld() {
        when(map.getActors()).thenReturn(List.of(actor));
        when(map.getTreasures()).thenReturn(List.of(treasure));
        when(actorMapper.toDTOs(List.of(actor))).thenReturn(ACTOR_DTOS);
        when(treasureMapper.toDTOs(List.of(treasure))).thenReturn(TREASURE_DTOS);
    }

    private static PlayerStateDTO stateOfLevel(int level) {
        return new PlayerStateDTO(level, 0, 100, 100, 100, 100, 100, 50, 50, List.of(), 0);
    }

}
