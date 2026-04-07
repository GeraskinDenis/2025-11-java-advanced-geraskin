package ru.geraskindenis.service;

import org.springframework.stereotype.Service;
import ru.geraskindenis.cache.Cache;

import java.util.Objects;
import java.util.function.Function;

@Service
public class CacheServiceImpl implements CacheService {

    private Cache<String, String> cache;

    public void setCache(Cache<String, String> cache) {
        this.cache = cache;
    }

    @Override
    public String get(String key, Function<String, String> loader) {
        checkingCache();
        return cache.get(key, loader);
    }

    @Override
    public void put(String key, String value) {
        checkingCache();
        cache.put(key, value);
    }

    @Override
    public boolean containsKey(String key) {
        checkingCache();
        return cache.containsKey(key);
    }

    @Override
    public void clear() {
        if (Objects.nonNull(cache)) {
            cache.clear();
        }
    }

    private void checkingCache() {
        if (Objects.isNull(cache)) {
            throw new IllegalStateException("Cache not initialized");
        }
    }
}
