package ru.geraskindenis.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.geraskindenis.dto.PersonDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @EqualsAndHashCode.Exclude
    private long id;

    private String guid;

    private String name;

    public PersonDto toDto() {
        return new PersonDto(id, guid, name);
    }
}
