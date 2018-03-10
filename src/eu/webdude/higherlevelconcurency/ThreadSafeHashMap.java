package eu.webdude.higherlevelconcurency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Trying to implement a thread-safe hashmap with read/write locks.
 * Yeah, I know that java.util.concurrent.ConcurrentHashMap exists.
 *
 * @param <K>
 * @param <V>
 */
public class ThreadSafeHashMap<K, V> {
    private Map<K, V> internalMap = new HashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();

    public void put(K key, V value) {
        wrapWriteOperation(key, value, internalMap::put);
    }

    public long size() {
        return wrapReadOperation(() -> internalMap.size());
    }

    public boolean contains(K key) {
        return wrapReadOperation(() -> internalMap.containsKey(key));
    }

    public V get(K key) {
        return wrapReadOperation(() -> internalMap.get(key));
    }

    private void wrapWriteOperation(K key, V value, BiConsumer<K, V> consumer) {
        try {
            writeLock.lock();
            consumer.accept(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    private <T> T wrapReadOperation(Supplier<T> supplier) {
        try {
            readLock.lock();
            return supplier.get();
        } finally {
            readLock.unlock();
        }
    }
}
