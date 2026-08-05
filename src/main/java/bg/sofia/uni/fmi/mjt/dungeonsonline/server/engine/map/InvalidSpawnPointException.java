package bg.sofia.uni.fmi.mjt.dungeonsonline.server.engine.map;

public class InvalidSpawnPointException extends RuntimeException {

    public InvalidSpawnPointException(String message) {
        super(message);
    }

    public InvalidSpawnPointException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidSpawnPointException(Throwable e) {
        super(e);
    }

}