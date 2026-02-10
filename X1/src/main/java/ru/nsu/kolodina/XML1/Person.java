package ru.nsu.kolodina.XML1;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Person {
    String id;
    String fullName;
    String firstName;
    String lastName;
    String gender;
    List<String> parentIds;
    String mother;
    String father;
    String spouseValue;
    Person spouse;
    Map<String, Person> parents;
    Map<String, String> children;
    Map<String, String> siblings;

    int childrenNumber;

    public Person() {
        this.parents = new HashMap<>();
        this.parentIds = new ArrayList<>();
        this.siblings = new HashMap<>();
        this.children = new HashMap<>();
    }
}
