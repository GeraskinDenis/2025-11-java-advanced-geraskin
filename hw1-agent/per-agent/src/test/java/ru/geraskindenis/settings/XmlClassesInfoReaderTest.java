package ru.geraskindenis.settings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.geraskindenis.settings.models.ClassInfo;

import java.nio.file.Path;
import java.util.List;

public class XmlClassesInfoReaderTest {

    @Test
    @DisplayName("Should read the file correctly `class-info.xml`")
    void readXmlSettings1() {
        XmlClassesInfoReader xmlClassesInfoReader = new XmlClassesInfoReader();
        List<ClassInfo> expected = getClassInfoList();
        List<ClassInfo> actual = xmlClassesInfoReader.read(
                Path.of("src/test/resources/classes-info.xml"));
        Assertions.assertArrayEquals(expected.toArray(), actual.toArray());
    }

    private static List<ClassInfo> getClassInfoList() {
        ClassInfo classInfo1 = new ClassInfo("MessagingServiceImpl", "ru.geraskindenis.services",
                20000L);
        classInfo1.setMethods(List.of(new ClassInfo.MethodInfo("getMsg"), new ClassInfo.MethodInfo("sendMsg")));
        ClassInfo classInfo2 = new ClassInfo("PersonService", "ru.geraskindenis.services",
                20000L);
        classInfo2.setMethods(List.of(new ClassInfo.MethodInfo("getPersonById"), new ClassInfo.MethodInfo("save")));
        return List.of(classInfo1, classInfo2);
    }
}
