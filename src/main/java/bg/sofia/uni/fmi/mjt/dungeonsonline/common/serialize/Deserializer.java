package bg.sofia.uni.fmi.mjt.dungeonsonline.common.serialize;

import java.io.IOException;

public interface Deserializer<T> {

    T deserialize() throws IOException;

}
