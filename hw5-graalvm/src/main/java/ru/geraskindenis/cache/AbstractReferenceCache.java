package ru.geraskindenis.cache;

import java.lang.ref.Reference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class AbstractReferenceCache<K, V> implements Cache<K, V> {

    protected final Map<K, Reference<V>> cache = new ConcurrentHashMap<>();

    protected abstract Reference<V> wrap(V value);

    @Override
    public V get(K key, Function<K, V> loader) {
        Reference<V> ref = cache.get(key);
        V value = (ref != null) ? ref.get() : null;
        if (value == null) {
            value = loader.apply(key);
            if (value != null) {
                put(key, value);
            }
        }
        return value;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, wrap(value));
    }

    @Override
    public boolean containsKey(K key) {
        Reference<V> ref = cache.get(key);
        return ref != null && ref.get() != null;
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
