package ru.geraskindenis.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileSystemServiceImpl implements FileSystemService {
    @Override
    public boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    @Override
    public boolean isRegularFile(Path path) {
        return Files.isRegularFile(path);
    }

    @Override
    public String readFile(Path path) throws IOException {
        return Files.readString(path);
    }

    @Override
    public Path resolve(Path base, String fileName) {
        return base.resolve(fileName);
    }
}
