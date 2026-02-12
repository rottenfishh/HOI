package ru.nsu.kolodina.XML1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class PeopleInfo {
    Map<String, Person> IdToPerson;
    Map<String, List<Person>> NameToPerson;
    List<Person> people;
}
