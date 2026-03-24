package ru.geraskindenis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.geraskindenis.models.Person;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {

    @EqualsAndHashCode.Exclude
    private long id;

    private String guid;

    private String name;

    public PersonDto(String name) {
        this.name = name;
    }

    public Person toPerson() {
        return new Person(id, guid, name);
    }
}
