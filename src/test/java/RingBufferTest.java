import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RingBufferTest {
    @Test
    void shouldOverwriteOldData() {
        int expectedSize = 3;
        RingBuffer<Integer> buffer = new RingBuffer<>(expectedSize);
        for (int i = 0; i < 4; i++) {
            buffer.add(i);
        }

        assertEquals(buffer.size(), expectedSize);
        for (int i = 1; i < 4; i++) {
            assertEquals(i, buffer.get());
        }
    }

    @Test
    void shouldDeleteData() {
        RingBuffer<Object> buffer = new RingBuffer<>(5);

        buffer.add(new Object());
        assertEquals(1, buffer.size());

        buffer.delete();
        assertEquals(0, buffer.size());

        Exception e = assertThrows(IllegalStateException.class, buffer::delete);
        assertEquals("Buffer is empty", e.getMessage());
    }

    @Test
    void shouldGetData() {
        RingBuffer<Object> buffer = new RingBuffer<>(5);
        Object expected = new Object();

        buffer.add(expected);
        assertEquals(1, buffer.size());

        Object actual = buffer.get();
        assertEquals(expected, actual);
        assertEquals(0, buffer.size());

        Exception e = assertThrows(IllegalStateException.class, buffer::get);
        assertEquals("Buffer is empty", e.getMessage());
    }

    @Test
    void shouldNotFailFromManyThreads() {
        int expectedSize = 50;
        RingBuffer<String> buffer = new RingBuffer<>(expectedSize);
        List<Thread> list = new ArrayList<>();

        for (int i = 0; i < 16; i++) {
            list.add(new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    buffer.add(Thread.currentThread() + ": " + j);
                }
            }));
        }

        list.forEach(Thread::start);
        list.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(expectedSize, buffer.size());

        list.clear();

        for (int i = 0; i < 5; i++) {
            list.add(new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    buffer.delete();
                }
            }));
        }

        list.forEach(Thread::start);
        list.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(25, buffer.size());

        list.clear();

        for (int i = 0; i < 5; i++) {
            list.add(new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    buffer.get();
                }
            }));
        }

        list.forEach(Thread::start);
        list.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(0, buffer.size());
    }
    
}
