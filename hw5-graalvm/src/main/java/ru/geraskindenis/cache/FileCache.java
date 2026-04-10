package ru.geraskindenis.cache;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class FileCache extends AbstractReferenceCache<String, String> {

    private final boolean useSoft;

    public FileCache(boolean useSoft) {
        this.useSoft = useSoft;
    }

    @Override
    protected Reference<String> wrap(String value) {
        return useSoft ? new SoftReference<>(value) : new WeakReference<>(value);
    }
}
