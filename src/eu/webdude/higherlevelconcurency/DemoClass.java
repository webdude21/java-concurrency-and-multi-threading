package eu.webdude.higherlevelconcurency;

import java.util.Random;
import java.util.concurrent.*;

public class DemoClass {

    private static final int TEST_BOUND = 1_000_000;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadSafeSingleton threadSafeSingleton = ThreadSafeSingleton.getInstance();
        System.out.println(threadSafeSingleton);

        ThreadSafeHashMap<Long, String> testMap = new ThreadSafeHashMap<>();

        Callable<String> task = () -> {
            Random rand = new Random();

            while (testMap.size() < TEST_BOUND) {
                long key = rand.nextInt(TEST_BOUND);
                testMap.put(key, Long.toString(key));
                if (!testMap.contains(key)) {
                    System.out.println("Failed to add key to map");
                }
            }

            return "Finished";
        };

        ExecutorService executorService = Executors.newFixedThreadPool(4);


        try {
            for (int i = 0; i < 4; i++) {
                Future<String> submit = executorService.submit(task);
                System.out.println(submit.get());
            }
        } finally {
            executorService.shutdown();
        }
    }
}
