package ru.geraskindenis.command;

import ru.geraskindenis.service.ConsoleService;

public class ExitCommand implements Command{

    private final ConsoleService console;

    private final Runnable onExit;

    public ExitCommand(ConsoleService console, Runnable onExit) {
        this.console = console;
        this.onExit = onExit;
    }

    @Override
    public void execute() {
        console.print("Exit...");
        onExit.run();
    }

    @Override
    public String getDescription() {
        return "Exit";
    }

    @Override
    public String getKeyword() {
        return "exit";
    }
}
