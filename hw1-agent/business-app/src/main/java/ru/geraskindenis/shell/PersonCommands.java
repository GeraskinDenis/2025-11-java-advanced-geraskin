package ru.geraskindenis.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.geraskindenis.dto.PersonDto;
import ru.geraskindenis.services.PersonService;

import java.util.stream.Collectors;

@Command(group = "Person commands", command = "person")
public class PersonCommands {

    private final String PERSON_PATTERN = "Person ID: %s Guid: %s Name: %s";

    private final PersonService personService;

    @Autowired
    public PersonCommands(PersonService personService) {
        this.personService = personService;
    }

    @Command(command = "del-by-guid", description = "delete a person by GUID")
    public String deleteByGuid(@Option(description = "person GUID", required = true, longNames = "GUID") String guid) {
        String msg;
        if (personService.deleteByGuid(guid)) {
            msg = "Person has been deleted.";
        } else {
            msg = "Not found Person by GUID.";
        }
        return msg;
    }

    @Command(command = "del-by-id", description = "delete a person by ID")
    public String deleteById(@Option(description = "person ID", required = true, longNames = "ID") long id) {
        String msg;
        if (personService.deleteById(id)) {
            msg = "Person has been deleted.";
        } else {
            msg = "Not found Person by ID: " + id;
        }
        return msg;
    }

    @Command(command = "find-by-guid", description = "Find a Person by GUID")
    public String findByGuid(@Option(description = "person GUID", required = true) String guid) {
        return personService.findByGuid(guid).map(p ->
                        PERSON_PATTERN.formatted(p.getId(), p.getGuid(), p.getName()))
                .orElse("Not found Person by GUID: " + guid);
    }

    @Command(command = "find-by-id", description = "Find a Person by ID")
    public String findById(@Option(description = "The Person ID", required = true) long id) {
        return personService.findById(id).map(p ->
                        PERSON_PATTERN.formatted(p.getId(), p.getGuid(), p.getName()))
                .orElse("Not found Person by ID : " + id);
    }

    @Command(command = "all", description = "show all persons")
    public String getAll() {

        String msg = personService.getAll().stream()
                .map(p -> PERSON_PATTERN.formatted(p.getId(), p.getGuid(), p.getName()))
                .collect(Collectors.joining("\n"));
        if (!msg.isEmpty()) {
            msg = "List of persons:\n" + msg;
        } else {
            msg = "The list of Person is empty.";
        }
        return msg;
    }

    @Command(command = "insert", description = "Add a new Person")
    public String insert(@Option(description = "New name of a Person", required = true, label = "name") String name) {
        PersonDto personDto = personService.save(new PersonDto(name));
        return "New person added:\n"
                + PERSON_PATTERN.formatted(personDto.getId(), personDto.getGuid(), personDto.getName());
    }

    @Command(command = "update", description = "Update an existing Person")
    public String update(@Option(description = "The Person ID", required = true, label = "id") long id,
                         @Option(description = "The name of a Person", required = true, label = "name") String name) {
        PersonDto personDto = personService.save(new PersonDto(id, "", name));
        return "The Person has been updated:\n"
                + PERSON_PATTERN.formatted(personDto.getId(), personDto.getGuid(), personDto.getName());
    }
}
