package ru.geraskindenis.agent;

import ru.geraskindenis.settings.AgentArgsParser;
import ru.geraskindenis.settings.AgentArgsParserImpl;
import ru.geraskindenis.settings.XMLReader;
import ru.geraskindenis.settings.XmlClassesInfoReader;
import ru.geraskindenis.settings.models.ClassInfo;
import ru.geraskindenis.transformer.ClassTransformer;
import ru.geraskindenis.utils.AgentLocation;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MethodCallCounterAgent {

    private static final String CLASS_FILE_NAME_XML = "classes-info.xml";

    public static final String ARG_CLASS_FILE = "CLASS_FILE";

    private static final Logger log = Logger.getLogger(MethodCallCounterAgent.class.getName());

    /**
     * Entrypoint into this agent in case of STATIC load
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("[Agent] Starting 'premain' method");
        instrument(agentArgs, inst);
    }

    /**
     * Entrypoint into this agent in case of DYNAMIC load
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        log.info("[Agent] Starting 'agentmain' method");
        instrument(agentArgs, inst);
    }

    private static void instrument(String agentArgs, Instrumentation inst) {

        AgentArgsParser agentArgsParser = new AgentArgsParserImpl();
        Map<String, String> agentArgsMap = agentArgsParser.parse(agentArgs);

        Path path = (!agentArgsMap.containsKey(ARG_CLASS_FILE))
                ? getDefaultPath() : Path.of(agentArgsMap.get(ARG_CLASS_FILE));

        XMLReader<List<ClassInfo>> xmlReader = new XmlClassesInfoReader();
        Map<String, ClassInfo> classesMap = xmlReader.read(path).stream()
                .filter(c -> !c.getMethods().isEmpty())
                .collect(Collectors.toMap(
                        c -> c.getPackageInfo()
                                .replace('.', '/') + "/" + c.getName(),
                        Function.identity(), (e, r) -> e));
        inst.addTransformer(new ClassTransformer(classesMap));
    }

    private static Path getDefaultPath() {
        Path classFilePath = AgentLocation.getFilePath(CLASS_FILE_NAME_XML);
        log.info("[Agent] Get default `%s` file path".formatted(classFilePath));
        return classFilePath;
    }
}
