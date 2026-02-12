package ru.nsu.kolodina.XML1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Collector {
    
    public Map<String, Person> mergeIds(PeopleInfo people) {
        Map<String, Person> idToPerson = new HashMap<>();
        for (Map.Entry<String, List<Person>> entry : people.IdToPerson.entrySet()) {
            Person fullPerson = new Person();
            Map<String, String> relatives = new HashMap<>();
            for (Person person : entry.getValue()) {
                if (person.getFullName() != null) {
                    fullPerson.setFullName(person.getFullName());
                }
                if (person.getGender() != null) {
                    fullPerson.setGender(person.getGender());
                }

            }

            List<Person> personFromName = people.getNameToPerson().get(fullPerson.getFullName());
            for (Person person : personFromName) { // if we merged this person by id alone, remove it from names list
                if (person.getId().equals(fullPerson.getId())) { // if it does have id, its already merged
                    people.getNameToPerson().remove(fullPerson.getFullName());
                }
            }
            people.getNameToPerson().get(fullPerson.getFullName()).add(fullPerson); // put full person idk

            if (idToPerson.containsKey(entry.getKey())) {
                System.err.println("duplicate id: " + entry.getKey());
            }
            idToPerson.put(entry.getKey(), fullPerson);
        }

        for (Person guy: idToPerson.values()) {
            mergeRelativesByIds(guy, people.getNameToPerson(), idToPerson);
        }

        mergePeopleByName(idToPerson, people.getNameToPerson(), people.getPeople());
        return idToPerson;
    }

    public Map<String, String> mergeRelativesByIds(Person person, Map<String, List<Person>> nameToPerson, Map<String, Person> idToPerson) {
        for (Map.Entry<String, String> relative: person.relativeToRole.entrySet()) { // merge relatives by id
            if (relative.getKey().startsWith("P")) {
                String name = idToPerson.get(relative.getKey()).getFullName();
                String role = relative.getValue();
                if (role.equals("parent") || role.equals("siblings") || role.equals("spouce")) {
                    if (name != null) {
                        String relativeRole = person.relativeToRole.get(name);
                        person.relativeToRole.put(relative.getKey(), relativeRole);
                    }
                }
                // if we already have relative id here, delete their name
                person.relativeToRole.remove(name);
            }
        }

        for (Map.Entry<String, String> relative: person.relativeToRole.entrySet()) {
            if (!relative.getKey().startsWith("P")) {
                // find their ids by name
                List<Person> suspects = nameToPerson.get(relative.getKey()); // нашли потенциальных родственников по имени
                for (Person dude : suspects) {
                    if (dude.getId() == null) {
                        continue;
                    }
                    String role = relative.getValue();

                    String ourDudeRole = dude.getRelativeToRole().get(person.id);
                    if (ourDudeRole == null) {
                        ourDudeRole = dude.getRelativeToRole().get(person.fullName);
                    }
                    if (ourDudeRole == null) {
                        continue;
                    }
                    if (ourDudeRole.equals("child") || ourDudeRole.equals("daughter") || ourDudeRole.equals("son")) {
                        person.relativeToRole.put(dude.getId(), role);
                    }
                }
                person.relativeToRole.remove(relative.getKey());
            }
        }
        return person.relativeToRole;
    }

    public void mergePeopleByName(Map<String, Person> idToPerson, Map<String, List<Person>> nameToPerson, List<Person> people) {
        System.out.println(nameToPerson);
        // используя вторичные признаки??? kill myself

        for (Map.Entry<String, List<Person>> entry : nameToPerson.entrySet()) {
            List<String> ids = new ArrayList<>();
            for (Person person : entry.getValue()) {

            }
        }
    }
}
