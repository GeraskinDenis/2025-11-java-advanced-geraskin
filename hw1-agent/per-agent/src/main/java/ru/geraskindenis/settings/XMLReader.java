package ru.geraskindenis.settings;

import java.nio.file.Path;

public interface XMLReader<T> {
    T read(Path path);
}
