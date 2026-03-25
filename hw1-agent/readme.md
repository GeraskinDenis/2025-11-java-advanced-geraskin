# hw1-agent
__Цель:__
Разработать JAVA-агент для подсчитывающий количество вхождений в методы.

## [business-app](business-app/business-app.md)
A simple console application for managing `Person` objects.

## [business-app-2](business-app-2/business-app-2.md)
This is an example of a simple console application for managing `Person` objects with a built
-in method call counter.

## [per-agent](per-agent/per-agent.md)
This JAVA agent adds a call counter to the methods.

## Launching
```shell
java -javaagent:./per-agent/target/per-agent-0.0.1-SNAPSHOT.jar -jar ./business-app/target/business-app-3.5.9.jar
```

## Launching the agent with debugging;
```shell
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005 -javaagent:./per-agent/target/per-agent-0.0.1-SNAPSHOT.jar -jar ./business-app/target/business-app-3.5.9.jar
```
[per-agent](.run/per-agent.run.xml) - open in __Run/Debug Configurations__. Settings for connecting to remote debugging.

## Example of the XML settings `classes-info.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<classes>
    <class name="PersonServiceImpl" package="ru.geraskindenis.services" duration="20000">
        <methods>
            <method>getAll</method>
            <method>findById</method>
            <method>save</method>
        </methods>
    </class>
    <class name="PersonRepositoryImpl" package="ru.geraskindenis.repository" duration="20000">
        <methods>
            <method>getAll</method>
        </methods>
    </class>
</classes>
```