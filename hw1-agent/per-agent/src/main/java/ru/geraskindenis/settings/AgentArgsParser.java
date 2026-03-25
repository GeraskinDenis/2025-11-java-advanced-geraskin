package ru.geraskindenis.settings;

import java.util.Map;

public interface AgentArgsParser {
    Map<String, String> parse(String agentArgs);
}
