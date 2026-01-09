package ru.geraskindenis.repositories;

import ru.geraskindenis.models.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository {

    boolean deleteByGuid(String guid);

    boolean deleteById(long id);

    List<Person> getAll();

    Optional<Person> findById(Long id);

    Optional<Person> findByGuid(String guid);

    Person save(Person person);
}
