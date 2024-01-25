import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class RingBuffer<T> {
    private final int size;
    private final Queue<T> queue;

    public RingBuffer(int size) {
        this.size = size;
        this.queue = new LinkedList<>();
    }

    public synchronized void add(T obj) {
        if (queue.size() == size) queue.poll();
        queue.add(obj);
    }

    public synchronized void delete() {
        if (queue.size() == 0) throw new IllegalStateException("Buffer is empty");
        queue.poll();
    }

    public synchronized T get() {
        if (queue.isEmpty()) throw new IllegalStateException("Buffer is empty");
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }

    @Override
    public String toString() {
        return queue.toString();
    }

}
