package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine;

import java.util.Set;

public record GameEvent(Set<Integer> recipients, String message) { }
