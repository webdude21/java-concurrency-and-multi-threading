package eu.webdude.higherlevelconcurency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParallelFindPrimesWithBarrier {

    private static final int SUB_TASK_COUNT = 4;
    private static final int MAX_PRIME_NUMBER = 100_000_000;
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(SUB_TASK_COUNT);

    public static void main(String[] args) throws InterruptedException {
        List<Callable<List<Integer>>> callables = new ArrayList<>();

        int chunk = MAX_PRIME_NUMBER / SUB_TASK_COUNT;

        for (int i = 1; i <= MAX_PRIME_NUMBER; i += chunk) {
            callables.add(new PrimeNumberWorker(cyclicBarrier, getRange(i, i + chunk)));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(SUB_TASK_COUNT);

        executorService.invokeAll(callables)
                .stream()
                .flatMap(callableResult -> {
                    try {
                        return callableResult.get().stream();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return Stream.empty();
                    }
                })
                .sorted()
                .forEach(System.out::println);
    }

    private static List<Integer> getRange(int start, int end) {
        return IntStream
                .range(start, end)
                .boxed()
                .collect(Collectors.toList());
    }
}
