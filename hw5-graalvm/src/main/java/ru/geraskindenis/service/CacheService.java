package ru.geraskindenis.service;

import java.util.function.Function;

public interface CacheService {

    String get(String key, Function<String, String> loader);

    void put(String key, String value);

    boolean containsKey(String key);

    void clear();
}
