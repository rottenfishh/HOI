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
    Map<String, List<String>> roleToIDName;
    Map<String, List<String>> roleToPerson;

    int childrenNumber;

    public Person() {
        this.roleToIDName = new HashMap<>();
        this.roleToPerson = new HashMap<>();
    }
}
