package eu.webdude.multithreading.consumerproducer;

public class SynchronizedConsumerProducer {

    private static final int BUFFER_SIZE = 100;

    private int count = 0;
    private int[] buffer = new int[BUFFER_SIZE];
    private final Object lock = new Object();

    public static void main(String[] args) {
        SynchronizedConsumerProducer naiveConsumerProducer = new SynchronizedConsumerProducer();
        naiveConsumerProducer.test();
    }

    private void test() {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();
        Thread producerThread = new Thread(producer::produce);
        Thread consumerThread = new Thread(consumer::consume);

        consumerThread.start();
        producerThread.start();
    }

    class Producer {
        void produce() {
            synchronized (lock) {
                while (isFull(buffer)) {
                    // DO NOTHING
                }

                printCount();
                buffer[count++] = 1;
            }
        }
    }

    private boolean isFull(int[] buffer) {
        return buffer[buffer.length - 1] == 1;
    }

    class Consumer {
        void consume() {
            synchronized (lock) {
                while (isEmpty(buffer)) {
                    // DO NOTHING
                }

                printCount();
                buffer[--count] = 0;
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
