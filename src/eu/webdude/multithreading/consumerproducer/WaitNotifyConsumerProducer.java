package eu.webdude.multithreading.consumerproducer;

public class WaitNotifyConsumerProducer {

    private static final int BUFFER_SIZE = 10;

    private int count = 0;
    private int[] buffer = new int[BUFFER_SIZE];
    private final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        WaitNotifyConsumerProducer naiveConsumerProducer = new WaitNotifyConsumerProducer();
        naiveConsumerProducer.test();
    }


    private Runnable runTimes(int timesToRun, InterruptableRunnable runnable) {
        return () -> {
            try {
                for (int i = 0; i < timesToRun; i++) {
                    runnable.run();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private void test() throws InterruptedException {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        Thread producerThread = new Thread(runTimes(50, producer::produce));
        Thread consumerThread = new Thread(runTimes(50, consumer::consume));

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();

        System.out.println("Buffer elements left: " + count);
    }

    class Producer {
        void produce() throws InterruptedException {
            synchronized (lock) {
                while (isFull(buffer)) {
                    lock.wait();
                }

                buffer[count++] = 1;
                printCount();
                lock.notify();
            }
        }
    }

    private boolean isFull(int[] buffer) {
        return buffer[buffer.length - 1] == 1;
    }

    class Consumer {
        void consume() throws InterruptedException {
            synchronized (lock) {
                while (isEmpty(buffer)) {
                    lock.wait();
                }

                buffer[--count] = 0;
                printCount();
                lock.notify();
            }
        }
    }

    private boolean isEmpty(int[] buffer) {
        return buffer[0] == 0;
    }

    private void printCount() {
        System.out.println(count);
    }
}
