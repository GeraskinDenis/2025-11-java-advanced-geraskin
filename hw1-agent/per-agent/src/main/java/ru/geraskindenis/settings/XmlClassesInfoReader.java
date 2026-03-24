package ru.geraskindenis.settings;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.geraskindenis.settings.models.ClassInfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class XmlClassesInfoReader implements XMLReader<List<ClassInfo>> {

    private static final Logger log = Logger.getLogger(XmlClassesInfoReader.class.getName());

    private static final String NODE_NAME_CLASS = "class";

    private static final String CLASS_ATTR_NAME = "name";

    private static final String CLASS_ATTR_PACKAGE = "package";

    private static final String CLASS_ATTR_DURATION = "duration";

    private static final String NODE_NAME_METHOD = "method";

    @Override
    public List<ClassInfo> read(Path filePath) {

        log.info("[AGENT] Reading XML-file");

        Objects.requireNonNull(filePath, "The 'filePath' parameter must not be null.");

        File file = filePath.toFile();

        if (!file.exists()) {
            throw new IllegalArgumentException("The '%s' file must exist.".formatted(file));
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.normalize();
            Element root = doc.getDocumentElement();
            log.info("[AGENT] XML-file has been read");
            return getClassesInfo(root.getElementsByTagName(NODE_NAME_CLASS));

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ClassInfo> getClassesInfo(NodeList classes) {
        Objects.requireNonNull(classes, "The 'classes' parameter must not be null.");

        List<ClassInfo> listOfClasses = new ArrayList<>();
        for (int i = 0; i < classes.getLength(); i++) {
            Element classInfo = (Element) classes.item(i);
            listOfClasses.add(getClassInfo(classInfo));
        }
        return listOfClasses;
    }

    private ClassInfo getClassInfo(Element element) {
        Objects.requireNonNull(element, "The 'element' parameter must not be null.");

        if (!element.getNodeName().equals(NODE_NAME_CLASS)) {
            throw new IllegalArgumentException("The 'element' parameter does not match the node name '%s'."
                    .formatted(NODE_NAME_CLASS));
        }

        if (element.getNodeType() != Node.ELEMENT_NODE) {
            throw new IllegalArgumentException("The 'element' parameter does not match the node type 'Element'.");
        }

        String className = element.getAttribute(CLASS_ATTR_NAME);
        if (className.isBlank()) {
            throw new IllegalArgumentException("The 'name' attribute for 'class' must have a value.");
        }

        String packageName = element.getAttribute(CLASS_ATTR_PACKAGE);
        if (packageName.isBlank()) {
            throw new IllegalArgumentException("The 'package' attribute for 'class' must have a value.");
        }

        String durationStr = element.getAttribute(CLASS_ATTR_DURATION);
        if (durationStr.isBlank()) {
            throw new IllegalArgumentException("The 'duration' attribute for 'class' must have a value.");
        }

        long duration;
        try {
            duration = Long.parseLong(durationStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("The string '%s' cannot be converted  to a number.");
        }

        ClassInfo classInfo = new ClassInfo(className, packageName, duration);
        classInfo.setMethods(getMethods(element.getElementsByTagName(NODE_NAME_METHOD)));
        return classInfo;
    }

    private List<ClassInfo.MethodInfo> getMethods(NodeList methods) {
        Objects.requireNonNull(methods, "The 'methods' parameter must not be null.");

        List<ClassInfo.MethodInfo> listOfMethods = new ArrayList<>();
        for (int i = 0; i < methods.getLength(); i++) {
            Element method = (Element) methods.item(i);
            listOfMethods.add(getMethod(method));
        }
        return listOfMethods;
    }

    private ClassInfo.MethodInfo getMethod(Element method) {
        Objects.requireNonNull(method, "The 'method' parameter must not be null.");

        if (!method.getTagName().equals(NODE_NAME_METHOD)) {
            throw new IllegalArgumentException("The 'method' parameter does not match the node name `%s`."
                    .formatted(NODE_NAME_METHOD));
        }

        if (method.getNodeType() != Node.ELEMENT_NODE) {
            throw new IllegalArgumentException("The 'method' parameter does not match the node type 'Element'.");
        }

        String methodName = null;

        NodeList nodeList = method.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                methodName = node.getTextContent();
            }
        }

        Objects.requireNonNull(methodName, "The XML classes-info file must not content an empty method name.");

        return new ClassInfo.MethodInfo(methodName);
    }
}
