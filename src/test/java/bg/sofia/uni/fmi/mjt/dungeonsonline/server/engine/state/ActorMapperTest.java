package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Minion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Player;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.position.Position;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActorMapperTest {

    private static final int PLAYER_ID = 1;
    private static final int MINION_ID = 10;

    private static final int ROW = 3;
    private static final int COL = 4;

    private static final Position POSITION = new Position(ROW, COL);

    @Mock
    private Player player;
    @Mock
    private Minion minion;

    private ActorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ActorMapper();
    }

    @Test
    void testToDTOMapsAPlayerToTheirIdAndPosition() {
        when(player.getId()).thenReturn(PLAYER_ID);
        when(player.getPosition()).thenReturn(POSITION);

        assertEquals(new ActorDTO(PLAYER_ID, ActorKind.PLAYER, ROW, COL), mapper.toDTO(player),
            "ActorMapper should map a player to their id, kind and position");
    }

    @Test
    void testToDTOMapsAMinionToTheMinionKind() {
        when(minion.getId()).thenReturn(MINION_ID);
        when(minion.getPosition()).thenReturn(POSITION);

        assertEquals(new ActorDTO(MINION_ID, ActorKind.MINION, ROW, COL), mapper.toDTO(minion),
            "ActorMapper should map a minion to its id, kind and position");
    }

    @Test
    void testToDTOsMapsEveryActorItWasGiven() {
        when(player.getId()).thenReturn(PLAYER_ID);
        when(player.getPosition()).thenReturn(POSITION);
        when(minion.getId()).thenReturn(MINION_ID);
        when(minion.getPosition()).thenReturn(POSITION);

        List<ActorDTO> mapped = mapper.toDTOs(List.<Actor>of(player, minion));

        assertEquals(2, mapped.size(), "ActorMapper should map every actor it was given");
        assertEquals(ActorKind.PLAYER, mapped.getFirst().kind(),
            "ActorMapper should keep the actors in the order they came in");
        assertEquals(ActorKind.MINION, mapped.getLast().kind(),
            "ActorMapper should keep the actors in the order they came in");
    }

}
