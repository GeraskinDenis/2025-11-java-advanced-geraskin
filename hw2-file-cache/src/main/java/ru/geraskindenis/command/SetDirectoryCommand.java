package ru.geraskindenis.command;

import ru.geraskindenis.service.ConsoleService;
import ru.geraskindenis.service.FileSystemService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

public class SetDirectoryCommand implements Command {

    private final ConsoleService console;

    private final FileSystemService fileSystem;

    private final AtomicReference<Path> currentDirectory;

    private final Runnable onDirectoryChanged;

    public SetDirectoryCommand(ConsoleService console, FileSystemService fileSystem, AtomicReference<Path> currentDirectory, Runnable onDirectoryChanged) {
        this.console = console;
        this.fileSystem = fileSystem;
        this.currentDirectory = currentDirectory;
        this.onDirectoryChanged = onDirectoryChanged;
    }

    @Override
    public void execute() {

        console.print("Enter the path to the file storage directory: ");
        String pathStr = console.readLine();
        ;
        Path dir = Paths.get(pathStr);
        if (fileSystem.isDirectory(dir)) {
            currentDirectory.set(dir);
            onDirectoryChanged.run();
            console.printf("The directory for storing files is installed: %s%n", dir.toAbsolutePath());
        } else {
            console.printf("Error: The directory does not exist.");
        }
    }

    @Override
    public String getDescription() {
        return "Setting a file storage directory";
    }

    @Override
    public String getKeyword() {
        return "set-dir";
    }
}
