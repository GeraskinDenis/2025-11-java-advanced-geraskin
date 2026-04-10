package ru.geraskindenis.cache;

import java.util.function.Function;

public interface Cache<K, V> {

    V get(K key, Function<K, V> loader);

    void put(K key, V value);

    boolean containsKey(K key);

    void clear();
}
