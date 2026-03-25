package ru.geraskindenis.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentLocation {

    private static final Logger log = Logger.getLogger(AgentLocation.class.getName());

    private static final Path PROGRAM_DIR;

    static {
        Path dir = null;
        try {
            ProtectionDomain protectionDomain = AgentLocation.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource != null) {
                URL location = codeSource.getLocation();
                Path path = urlToPath(location);
                if (!Files.isDirectory(path) && Files.exists(path)) {
                    dir = path.getParent();
                } else {
                    dir = path;
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
        if (dir == null) {
            dir = Paths.get("").toAbsolutePath();
        }
        PROGRAM_DIR = dir;
    }

    private AgentLocation() {
    }

    public static Path getProgramDir() {
        return PROGRAM_DIR;
    }

    public static Path getFilePath(String fileName) {
        Objects.requireNonNull(fileName, "The `fileName` parameter must not have a null value.");
        return PROGRAM_DIR.resolve(fileName);
    }

    private static Path urlToPath(URL location) throws URISyntaxException {
        if ("jar".equals(location.getProtocol())) {
            String spec = location.toString();
            int separator = spec.indexOf("!/");
            if (separator > 0) {
                String jarPart = spec.substring(4, separator); // обрезаем "jar:"
                return Paths.get(new URI(jarPart));
            }
        }
        return Paths.get(location.toURI());
    }
}
