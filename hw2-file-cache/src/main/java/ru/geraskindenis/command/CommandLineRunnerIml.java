package ru.geraskindenis.command;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.geraskindenis.cache.FileCache;
import ru.geraskindenis.service.CacheService;
import ru.geraskindenis.service.CacheServiceImpl;
import ru.geraskindenis.service.ConsoleService;
import ru.geraskindenis.service.FileSystemService;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class CommandLineRunnerIml implements CommandLineRunner {

    private final ConsoleService console;
    private final CacheService cacheService;
    private final FileSystemService fileSystem;
    private final Map<String, Command> commands = new HashMap<>();
    private final AtomicReference<Path> currentDirectory = new AtomicReference<>();
    private boolean running = true;

    public CommandLineRunnerIml(ConsoleService console, CacheService cacheService, FileSystemService fileSystem) {
        this.console = console;
        this.cacheService = cacheService;
        this.fileSystem = fileSystem;
        initCommands();
    }

    @Override
    public void run(String... args) {
        console.print("=== File cache with SoftReference/WeakReference ===");
        chooseCacheType();
        mainLoop();
    }

    private void initCommands() {
        Runnable clearCache = cacheService::clear;
        addCommand(new SetDirectoryCommand(console, fileSystem, currentDirectory, clearCache));
        addCommand(new LoadFileCommand(console, cacheService, fileSystem, currentDirectory));
        addCommand(new GetFileCommand(console, cacheService, fileSystem, currentDirectory));
        addCommand(new ExitCommand(console, () -> running = false));
    }

    private void addCommand(Command cmd) {
        commands.put(cmd.getKeyword(), cmd);
    }

    private void chooseCacheType() {
        console.print("Select the link type (1 - Soft, 2 - Weak): ");
        String choice = console.readLine();
        boolean useSoft = !"2".equals(choice);
        FileCache cache = new FileCache(useSoft);
        if (cacheService instanceof CacheServiceImpl) {
            ((CacheServiceImpl) cacheService).setCache(cache);
        }
        console.printf("Using a " + (useSoft ? "SoftReference%n" : "WeakReference%n"));
    }

    private void mainLoop() {
        while (running) {
            printMenu();
            String inputCommand = console.readLine().trim().toLowerCase();
            Command command = commands.get(inputCommand);
            if (Objects.isNull(command)) {
                console.print("Unknown command. Try again.");
            } else {
                command.execute();
            }
        }
        console.close();
    }

    private void printMenu() {
        console.print("Available command:");
        for (Command command : commands.values()) {
            console.printf(" `%s` - %s%n", command.getKeyword(), command.getDescription());
        }
        console.print("Enter the command: ");
    }
}