package ru.geraskindenis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geraskindenis.agent.MonitoringHelper;
import ru.geraskindenis.dto.PersonDto;
import ru.geraskindenis.models.Person;
import ru.geraskindenis.repository.PersonRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private static final long startTime = System.currentTimeMillis();

    private static final long duration = 20000L;

    private static ConcurrentHashMap<String, AtomicLong> methodCounters = new ConcurrentHashMap<>();

    private static AtomicBoolean needToSave;

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
        if ((System.currentTimeMillis() - startTime) < duration) {
            MonitoringHelper.increment(methodCounters, "getAll");
        } else {
            saveDataIfNeeded();
        }

        return personRepository.getAll().
                stream().
                map(Person::toDto).
                collect(Collectors.toList());
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
        if (Objects.isNull(personDto.getGuid()) || personDto.getGuid().isEmpty()) {
            personDto.setGuid(UUID.randomUUID().toString());
        }
        return personRepository.save(personDto.toPerson()).toDto();
    }

    private void saveDataIfNeeded() {
        if (getNeedToSave().getPlain()) {
            if (getNeedToSave().compareAndSet(true, false)) {
                MonitoringHelper.saveData("ru.geraskindenis.services.PersonServiceImpl",
                        startTime, duration, methodCounters);
            }
        }
    }

    private static ConcurrentHashMap<String, AtomicLong> getMethodCounters() {
        ConcurrentHashMap<String, AtomicLong> map = methodCounters;
        if (map == null) {
            synchronized (PersonServiceImpl.class) {
                map = methodCounters;
                if (map == null) {
                    map = new ConcurrentHashMap<>();
                    methodCounters = map;
                }
            }
        }
        return map;
    }

    private static AtomicBoolean getNeedToSave() {
        AtomicBoolean atomicBoolean = needToSave;
        if (atomicBoolean == null) {
            synchronized (PersonServiceImpl.class) {
                atomicBoolean = needToSave;
                if (atomicBoolean == null) {
                    atomicBoolean = new AtomicBoolean(true);
                    needToSave = atomicBoolean;
                }
            }
        }
        return atomicBoolean;
    }
}
