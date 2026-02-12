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
    Map<String, String> relativeToRole; // one relative can have only one role. however, relative can be id or name. need to merge them.
    // in the end there will be the most specific role
    int childrenNumber;

    public Person() {
        this.relativeToRole = new HashMap<>();
    }
}
