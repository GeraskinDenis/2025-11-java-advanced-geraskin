package ru.geraskindenis.services;

import ru.geraskindenis.dto.PersonDto;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    boolean deleteByGuid(String guid);

    boolean deleteById(long id);

    List<PersonDto> getAll();

    Optional<PersonDto> findById(long id);

    Optional<PersonDto> findByGuid(String guid);

    PersonDto save(PersonDto personDto);
}
