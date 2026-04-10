package ru.geraskindenis.service;

import java.io.IOException;
import java.nio.file.Path;

public interface FileSystemService {

    boolean isDirectory(Path path);

    boolean isRegularFile(Path path);

    String readFile(Path path) throws IOException;

    Path resolve(Path base, String fileName);
}
