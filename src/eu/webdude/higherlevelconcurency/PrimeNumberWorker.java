package eu.webdude.higherlevelconcurency;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

public class PrimeNumberWorker implements Callable<List<Integer>> {

    private CyclicBarrier barrier;
    private List<Integer> integerList;

    public PrimeNumberWorker(CyclicBarrier barrier, List<Integer> integerList) {
        this.barrier = barrier;
        this.integerList = integerList;
    }

    @Override
    public List<Integer> call() throws Exception {
        List<Integer> result = findPrimes(integerList);
        barrier.await();
        return result;
    }

    private List<Integer> findPrimes(List<Integer> integerList) {
        return integerList.stream()
                .filter(this::isPrime)
                .collect(Collectors.toList());

    }

    private boolean isPrime(Integer n) {
        double max = Math.sqrt(n);
        for (int candidate = 2; candidate <= max; candidate++) {
            if (n % candidate == 0) {
                return false;
            }
        }

        return true;
    }
}
