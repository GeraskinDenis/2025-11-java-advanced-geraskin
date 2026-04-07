package ru.geraskindenis.command;

public interface Command {

    void execute();

    String getDescription();

    String getKeyword();
}
