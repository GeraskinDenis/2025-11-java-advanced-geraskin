package ru.geraskindenis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geraskindenis.dto.PersonDto;
import ru.geraskindenis.models.Person;
import ru.geraskindenis.repository.PersonRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public boolean deleteByGuid(String guid) {
        return personRepository.deleteByGuid(guid);
    }

    @Override
    public boolean deleteById(long id) {
        return personRepository.deleteById(id);
    }

    @Override
    public List<PersonDto> getAll() {
        return personRepository.getAll().stream().map(Person::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<PersonDto> findById(long id) {
        return personRepository.findById(id).map(Person::toDto);
    }

    @Override
    public Optional<PersonDto> findByGuid(String guid) {
        return personRepository.findByGuid(guid).map(Person::toDto);
    }

    @Override
    public PersonDto save(PersonDto personDto) {
        if(Objects.isNull(personDto.getGuid()) || personDto.getGuid().isEmpty()) {
            personDto.setGuid(UUID.randomUUID().toString());
        }
        return personRepository.save(personDto.toPerson()).toDto();
    }
}
