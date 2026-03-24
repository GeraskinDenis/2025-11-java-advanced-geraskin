package ru.geraskindenis.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.geraskindenis.dto.PersonDto;
import ru.geraskindenis.models.Person;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@SpringBootTest(properties = {"spring.shell.interactive.enabled=false"})
public class PersonServiceImplTest {

    private static final int NUMBER_OF_PERSONS = 10;

    @Autowired
    private PersonService personService;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @DisplayName("Should correctly delete a person by ID")
    void deleteByGuidTestCase1() {
        List<PersonDto> persons = createPersons().stream().map(Person::toDto).map(personService::save).toList();
        persons.forEach(p -> Assertions.assertTrue(personService.deleteByGuid(p.getGuid())));
        Assertions.assertEquals(0, personService.getAll().size());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @DisplayName("Should correctly delete a person by ID")
    void deleteByIdTestCase1() {
        List<PersonDto> persons = createPersons().stream().map(Person::toDto).map(personService::save).toList();
        persons.forEach(p -> Assertions.assertTrue(personService.deleteById(p.getId())));
        Assertions.assertEquals(0, personService.getAll().size());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @DisplayName("Should correctly find a person by ID")
    void findByIdTestCase1() {
        List<PersonDto> persons = createPersons().stream().map(Person::toDto).map(personService::save).toList();
        persons.forEach(expected -> {
            PersonDto actual = personService.findById(expected.getId()).orElseThrow();
            Assertions.assertEquals(expected, actual);
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @DisplayName("Should not find a person by a non-existent ID")
    void findByIdTestCase2() {
        Assertions.assertEquals(Optional.empty(), personService.findById(9999L));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @DisplayName("Should correctly find a person by ID")
    void findByGuidTestCase1() {
        List<PersonDto> persons = createPersons().stream().map(Person::toDto).map(personService::save).toList();
        persons.forEach(expected -> {
            PersonDto actual = personService.findByGuid(expected.getGuid()).orElseThrow();
            Assertions.assertEquals(expected, actual);
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @DisplayName("Should save a person correctly")
    void saveTestCase1() {
        PersonDto savedPerson = personService.save(new Person(0, UUID.randomUUID().toString(), "New person name").toDto());
        Assertions.assertTrue(savedPerson.getId() > 0);
        PersonDto foundPerson = personService.findById(savedPerson.getId()).orElseThrow();
        Assertions.assertEquals(savedPerson, foundPerson);
    }

    private static List<Person> createPersons() {
        return LongStream.range(1, NUMBER_OF_PERSONS).boxed()
                .map(id -> new Person(0, UUID.randomUUID().toString(), "PersonName-" + id))
                .collect(Collectors.toList());
    }
}
