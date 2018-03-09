package eu.webdude.multithreading.longwrapper;

import java.util.concurrent.atomic.AtomicLong;

public class LongWrapper {

    public static void main(String[] args) throws InterruptedException {
        LongWrapper longWrapper = new LongWrapper(0);

        Runnable runnable = () -> {
            for (int i = 0; i < 100_000; i++) {
                longWrapper.incrementValue();
            }
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(longWrapper.getValue());
    }

    private AtomicLong l;

    public LongWrapper(long l) {
        this.l = new AtomicLong(l);
    }

    public long getValue() {
        return l.longValue();
    }

    public void incrementValue() {
        l.getAndIncrement();
    }
}
