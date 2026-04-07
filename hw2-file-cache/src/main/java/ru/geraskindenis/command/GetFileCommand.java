package ru.geraskindenis.command;

import ru.geraskindenis.service.CacheService;
import ru.geraskindenis.service.ConsoleService;
import ru.geraskindenis.service.FileSystemService;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class GetFileCommand implements Command {

    private final ConsoleService console;

    private final CacheService cacheService;

    private final FileSystemService fileSystem;

    private final AtomicReference<Path> currentDirectory;

    public GetFileCommand(ConsoleService console, CacheService cacheService,
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

        console.print("Input the file name: ");
        String fileName = console.readLine();
        Function<String, String> loader = name -> {
            try {
                return fileSystem.readFile(dir.resolve(name));
            } catch (Exception e) {
                console.printf("File reading error: %s%n", e.getMessage());
                return null;
            }
        };
        String content = cacheService.get(fileName, loader);
        console.print("");
        if (content != null) {
            console.printf("Contents of the file `%s`:%n", fileName);
            console.print("-".repeat(100));
            console.printf("%s%n", content);
            console.print("-".repeat(100));
            console.print("");
        } else {
            console.print("The contents of the file could not be retrieved.");
        }
    }

    @Override
    public String getDescription() {
        return "Retrieving file contents from the cache.";
    }

    @Override
    public String getKeyword() {
        return "get-file";
    }
}
