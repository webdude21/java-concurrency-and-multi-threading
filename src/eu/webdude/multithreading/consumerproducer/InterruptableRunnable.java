package eu.webdude.multithreading.consumerproducer;

@FunctionalInterface
public interface InterruptableRunnable {
    void run() throws InterruptedException;
}
