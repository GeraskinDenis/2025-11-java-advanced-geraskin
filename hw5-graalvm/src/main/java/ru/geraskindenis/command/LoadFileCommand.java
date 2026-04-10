package ru.geraskindenis.command;

import ru.geraskindenis.service.CacheService;
import ru.geraskindenis.service.ConsoleService;
import ru.geraskindenis.service.FileSystemService;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class LoadFileCommand implements Command {

    private final ConsoleService console;

    private final CacheService cacheService;

    private final FileSystemService fileSystem;

    private final AtomicReference<Path> currentDirectory;

    public LoadFileCommand(ConsoleService console, CacheService cacheService,
                           FileSystemService fileSystem, AtomicReference<Path> currentDirectory) {
        this.console = console;
        this.cacheService = cacheService;
        this.fileSystem = fileSystem;
        this.currentDirectory = currentDirectory;
    }

    @Override
    public void execute() {

        Path dir = currentDirectory.get();
        if (dir == null) {
            console.print("The file storage directory is not configured.");
            return;
        }

        console.print("Input the file name (example: names.txt): ");
        String fileName = console.readLine();
        Path filePath = fileSystem.resolve(dir, fileName);
        if (!fileSystem.isRegularFile(filePath)) {
            console.printf("File not found: `%s`", filePath);
            return;
        }

        Function<String, String> loader = name -> {
            try {
                return fileSystem.readFile(dir.resolve(name));
            } catch (Exception e) {
                console.printf("Error of read the file: %s%n", e.getMessage());
                return null;
            }
        };

        String content = cacheService.get(fileName, loader);
        if (content != null) {
            console.printf("The file `%s` is loaded into the cache.%n", fileName);
        } else {
            console.print("File upload error.");
        }
    }

    @Override
    public String getDescription() {
        return "Uploading the file to the cache.";
    }

    @Override
    public String getKeyword() {
        return "load-file";
    }
}
