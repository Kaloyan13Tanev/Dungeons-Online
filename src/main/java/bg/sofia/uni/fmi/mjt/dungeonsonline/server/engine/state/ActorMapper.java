package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.state;

import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.actor.Minion;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.ActorDTO;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto.Mapper;
import bg.sofia.uni.fmi.mjt.dungeonsonline.shared.kind.ActorKind;

public class ActorMapper implements Mapper<Actor, ActorDTO> {

    @Override
    public ActorDTO toDTO(Actor actor) {
        return new ActorDTO(
            actor.getId(),
            actor instanceof Minion ? ActorKind.MINION : ActorKind.PLAYER,
            actor.getPosition().row(),
            actor.getPosition().col()
        );
    }

}
