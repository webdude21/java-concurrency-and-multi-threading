package eu.webdude.higherlevelconcurency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {
    private static volatile AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {

        class Incrementer implements Runnable {

            @Override
            public void run() {
                for (int i = 0; i < 10_000; i++) {
                    counter.incrementAndGet();
                }
            }
        }

        class Decrementer implements Runnable {

            @Override
            public void run() {
                for (int i = 0; i < 10_000; i++) {
                    counter.decrementAndGet();
                }
            }
        }


        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            futures.add(executorService.submit(new Incrementer()));
        }


        for (int i = 0; i < 4; i++) {
            futures.add(executorService.submit(new Decrementer()));
        }

        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        System.out.println(counter);
    }
}
