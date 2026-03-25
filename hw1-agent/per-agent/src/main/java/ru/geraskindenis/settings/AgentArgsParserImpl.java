package ru.geraskindenis.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class AgentArgsParserImpl implements AgentArgsParser {

    private static final Logger log = Logger.getLogger(AgentArgsParserImpl.class.getName());

    private static final Pattern AGETNT_ARGUMENT_PATTERN = Pattern.compile("^[a-zA-Z0-1_-]+=[^=]+$");

    @Override
    public Map<String, String> parse(String agentArgs) {

        Map<String, String> agentArgsMap = new HashMap<>();

        if (Objects.isNull(agentArgs) || agentArgs.isBlank()) {
            log.info("[Agent] The agent is running without arguments.");
            return agentArgsMap;
        }

        String[] lines = agentArgs.split(",");
        for (String line : lines) {
            if (!AGETNT_ARGUMENT_PATTERN.matcher(line).matches()) {
                throw new IllegalArgumentException("This '%s' argument does not match the '%s' pattern."
                        .formatted(line, AGETNT_ARGUMENT_PATTERN.pattern()));
            }
            String[] parameter = line.split("=");

            if ((parameter.length != 2) || (Objects.isNull(parameter[0])) || (parameter[0].isBlank())
                    || (Objects.isNull(parameter[1])) || (parameter[1].isBlank())) {
                throw new IllegalArgumentException("This '%s' argument does not match the '%s' pattern."
                        .formatted(line, AGETNT_ARGUMENT_PATTERN.pattern()));
            }
            agentArgsMap.put(parameter[0].trim().toUpperCase(), parameter[1].trim());
        }
        log.info("[Agent] The agent is running with arguments.");
        return agentArgsMap;
    }
}
