package eu.webdude.higherlevelconcurency;

import eu.webdude.multithreading.consumerproducer.InterruptableRunnable;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConsumerProducerWithExplicitLock {

    private static final int BUFFER_SIZE = 10;

    private int count = 0;
    private int[] buffer = new int[BUFFER_SIZE];
    private final Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        ConsumerProducerWithExplicitLock demo = new ConsumerProducerWithExplicitLock();
        demo.test();
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
        ConsumerProducerWithExplicitLock.Producer producer = new ConsumerProducerWithExplicitLock.Producer();
        ConsumerProducerWithExplicitLock.Consumer consumer = new ConsumerProducerWithExplicitLock.Consumer();

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
            try {
                lock.lock();

                if (isFull(buffer)) {
                    notFull.await();
                }

                buffer[count++] = 1;
                printCount();
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    private boolean isFull(int[] buffer) {
        return buffer[buffer.length - 1] == 1;
    }

    class Consumer {
        void consume() throws InterruptedException {
            try {
                lock.lock();

                if (isEmpty(buffer)) {
                    notEmpty.await();
                }

                buffer[--count] = 0;
                printCount();
                notFull.signal();
            } finally {
                lock.unlock();
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
