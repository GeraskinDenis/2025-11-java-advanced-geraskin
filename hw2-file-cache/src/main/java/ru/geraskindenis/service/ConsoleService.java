package ru.geraskindenis.service;

public interface ConsoleService {

    void print(String message);

    void printf(String format, Object... args);

    String readLine();

    void close();
}
