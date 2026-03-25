package ru.geraskindenis.agent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MonitoringHelper {

    // Потокобезопасный инкремент счётчика для метода
    public static void increment(ConcurrentHashMap<String, AtomicLong> counters, String methodName) {
        counters.computeIfAbsent(methodName, k -> new AtomicLong(0)).incrementAndGet();
    }

    // Сохранение данных в файл
    public static void saveData(String className, long startTime, long duration,
                                ConcurrentHashMap<String, AtomicLong> methodCounters) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss:SSS");
        String dateFrom = Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(formatter);
        String dateTo = Instant.ofEpochMilli(startTime + duration).atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(formatter);
        String text = String.format("""
                Class: %s
                Period: from %s to %s Duration: %s
                """, className, dateFrom, dateTo, duration);
        String fileName = className + ".log";
        StringBuilder sb = new StringBuilder(text);
        for (Map.Entry<String, AtomicLong> entry : methodCounters.entrySet()) {
            sb.append("\tMethod: `").append(entry.getKey()).append("()' Number of calls: ")
                    .append(entry.getValue().get()).append("\n");
        }
        try {
            Files.writeString(Path.of(fileName), sb.toString(),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}