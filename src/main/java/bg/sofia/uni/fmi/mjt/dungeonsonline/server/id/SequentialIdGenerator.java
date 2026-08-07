package bg.sofia.uni.fmi.mjt.dungeonsonline.server.id;

public class SequentialIdGenerator implements IdGenerator<Integer> {

    private int currentId;

    public SequentialIdGenerator() {
        this.currentId = 0;
    }

    public SequentialIdGenerator(int startId) {
        this.currentId = startId;
    }

    @Override
    public synchronized Integer acquire() {
        return currentId++;
    }
}
