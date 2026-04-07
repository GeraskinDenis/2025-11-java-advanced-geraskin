package ru.geraskindenis.service;

import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class ConsoleServiceImpl implements ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public void close() {
        scanner.close();
    }
}
