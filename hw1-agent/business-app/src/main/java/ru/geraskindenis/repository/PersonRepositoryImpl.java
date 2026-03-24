package ru.geraskindenis.repository;

import org.springframework.stereotype.Repository;
import ru.geraskindenis.models.Person;
import ru.geraskindenis.repository.exceptions.RepositoryException;

import java.util.*;

@Repository
public class PersonRepositoryImpl implements PersonRepository {

    private final Map<Long, Person> repository;

    private long lastId;

    public PersonRepositoryImpl() {
        this.lastId = 0L;
        this.repository = new HashMap<>() {
        };
    }

    @Override
    public boolean deleteByGuid(String guid) {
        return Objects.nonNull(repository.remove(getIdByGuid(guid)));
    }

    @Override
    public boolean deleteById(long id) {
        return Objects.nonNull(repository.remove(id));
    }

    @Override
    public List<Person> getAll() {
        return repository.values().stream().toList();
    }

    @Override
    public Optional<Person> findById(Long id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public Optional<Person> findByGuid(String guid) {
        return repository.values().stream().filter(p -> p.getGuid().equals(guid)).findFirst();
    }

    @Override
    public Person save(Person person) {
        if (person.getId() == 0) {
            person.setId(++lastId);
        } else {
            checkId(person.getId());
        }
        repository.put(person.getId(), person);
        return person;
    }

    private long getIdByGuid(String guid) {
        return repository.values().stream().filter(p -> p.getGuid().equals(guid))
                .findFirst().map(Person::getId).orElse(0L);
    }

    private void checkId(long id) {
        if (Objects.isNull(repository.get(id))) {
            throw new RepositoryException("ID does not exists: " + id);
        }
    }
}
