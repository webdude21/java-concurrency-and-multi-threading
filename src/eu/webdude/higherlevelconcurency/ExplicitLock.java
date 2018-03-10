package eu.webdude.higherlevelconcurency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExplicitLock {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();

        try {
            lock.lock();
            // do stuff
        } finally {
            lock.unlock();
        }
    }
}
