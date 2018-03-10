package eu.webdude.higherlevelconcurency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeSingleton {

    private static ThreadSafeSingleton instance;
    private static Lock lock = new ReentrantLock();

    public static ThreadSafeSingleton getInstance() {
        try {
            lock.lock();

            if (instance == null) {
                instance = new ThreadSafeSingleton();
            }

            return instance;
        } finally {
            lock.unlock();
        }
    }
}
