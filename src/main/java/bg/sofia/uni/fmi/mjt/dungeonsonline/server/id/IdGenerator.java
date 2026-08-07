package bg.sofia.uni.fmi.mjt.dungeonsonline.server.id;

public interface IdGenerator<T> {

    T acquire();

}
