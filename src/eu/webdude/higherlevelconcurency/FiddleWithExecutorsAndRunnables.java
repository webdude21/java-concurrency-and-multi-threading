package eu.webdude.higherlevelconcurency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class FiddleWithExecutorsAndRunnables {

    private static volatile AtomicLong count = new AtomicLong(0);

    public static void main(String[] args) {
        Runnable task = () -> {
            for (int i = 0; i < 100_000_000; i++) {
                count.getAndIncrement();
            }

            System.out.println(count);
        };

        ExecutorService service = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            service.execute(task);
        }

        service.shutdown();
    }
}
