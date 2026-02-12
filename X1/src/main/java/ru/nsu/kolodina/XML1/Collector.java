package ru.nsu.kolodina.XML1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Collector {
    
    public Map<String, Person> collectIds(PeopleInfo people) {
        Map<String, Person> idToPerson = new HashMap<>();
        for (Map.Entry<String, List<Person>> entry : people.IdToPerson.entrySet()) {
            Person fullPerson = new Person();
            for (Person person : entry.getValue()) {
                if (person.getFullName() != null) {
                    fullPerson.setFullName(person.getFullName());
                }
                if (person.getGender() != null) {
                    fullPerson.setGender(person.getGender());
                }
                for (Map.Entry<String, List<String>> relatives: person.getRoleToIDName().entrySet()) {
                    fullPerson.getRoleToIDName().putIfAbsent(relatives.getKey(), new ArrayList<>());
                    for (String id : relatives.getValue()) {
                        fullPerson.getRoleToIDName().get(relatives.getKey()).add(id);
                    }
                }
            }
            idToPerson.put(entry.getKey(), fullPerson);
        }
        return idToPerson;
    }

    public void mergeRelatives(Person person){
        Person mergedPerson = new Person();
        List<String> parents = person.getRoleToIDName().get("parent");
        List<String> motherEntries = person.getRoleToIDName().get("mother");
        List<String> fatherEntries = person.getRoleToIDName().get("father");
        for (String parent : parents) {

        }
    }
}
