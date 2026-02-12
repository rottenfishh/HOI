package ru.nsu.kolodina.XML1;

import lombok.AllArgsConstructor;

import java.util.*;

@AllArgsConstructor
public class Collector {
    PeopleInfo peopleInfo;

    public Map<String, Person> mergeIds() {
        Map<String, Person> idToPerson = new HashMap<>();
        for (Map.Entry<String, Person> entry : peopleInfo.IdToPerson.entrySet()) {
            Person fullPerson = entry.getValue();
            Map<String, String> relatives = new HashMap<>();
            List<Person> personFromName = peopleInfo.getNameToPerson().get(fullPerson.getFullName());
            for (Person person : personFromName) { // if we merged this person by id alone, remove it from names list
                if (person.getId().equals(fullPerson.getId())) { // if it does have id, its already merged
                    peopleInfo.getNameToPerson().get(fullPerson.getFullName()); // dont remove this
                }
            }
            peopleInfo.getNameToPerson().get(fullPerson.getFullName()).add(fullPerson); // put full person idk

            if (idToPerson.containsKey(entry.getKey())) {
                System.err.println("duplicate id: " + entry.getKey());
            }
            idToPerson.put(entry.getKey(), fullPerson);
        }

        mergePeopleWithOnlyName(idToPerson, peopleInfo.getNameToPerson(), peopleInfo.getPeople());

        for (Person guy: idToPerson.values()) {
            mergeRelativesByIds(guy, peopleInfo.getNameToPerson(), idToPerson);
        }

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
                    person.relativeToRole.put(dude.getId(), role);
                }
                person.relativeToRole.remove(relative.getKey());
            }
        }
        return person.relativeToRole;
    }

    public void mergePeopleWithOnlyName(Map<String, Person> idToPerson, Map<String, List<Person>> nameToPerson, List<Person> people) {
        System.out.println(nameToPerson);
        // используя вторичные признаки??? kill myself

        for (Map.Entry<String, List<Person>> entry : nameToPerson.entrySet()) {
            List<Person> ids = new ArrayList<>();
            List<Person> noIds = new ArrayList<>();
            for (Person person : entry.getValue()) {
                if (person.getId() != null) {
                    ids.add(person);
                } else {
                    noIds.add(person);
                }
            }
            for (Person person : noIds) {
                for (Person id: ids) {
                    int childNum1 = person.getChildrenNumber();
                    int childNum2 = id.getChildrenNumber();
                    if (childNum1 != 0 && childNum2 != 0) {
                        if (childNum1 != childNum2) {
                            continue;
                        }
                    }
                    if (!(person.getGender().equals(id.getGender()))) {
                        continue;
                    }
                    if (!validateRelatives(person, id)) {
                        continue;
                    }
                    Person fullPerson = mergePersonToId(person, id);
                }
            }
        }
    }

    public boolean validateRelatives(Person person1, Person person2) {
        if (!Objects.equals(person1.getMother(), "") && !Objects.equals(person2.getMother(), "")) {
            String mother1 = person1.getMother();
            String mother2 = person2.getMother();
            if (!isSamePerson(mother1, mother2)) {
                return false;
            }
        }
        if (!Objects.equals(person1.getFather(), "") && !Objects.equals(person2.getFather(), "")) {
            String father1 = person1.getFather();
            String father2 = person2.getFather();
            if (!isSamePerson(father1, father2)) {
                return false;
            }
        }
        if (!Objects.equals(person1.getSpouce(), "") && !Objects.equals(person2.getSpouce(), "")) {
            String spouse1 = person1.getSpouce();
            String spouse2 = person2.getSpouce();
            if (!isSamePerson(spouse1, spouse2)) {
                return false;
            }
        }
        return true;
    }

    public boolean isSamePerson(String name1, String name2) {
        if (name1.startsWith("P") && name2.startsWith("P")) {
            return name1.equals(name2);
        }
        else if (!name1.startsWith("P") && !name2.startsWith("P")) {
            return name1.equals(name2);
        } else {
            // idk find mother name. check if its the same person
            if (name1.startsWith("P")) {
                String nameFromId1 = peopleInfo.IdToPerson.get(name1).getFullName();
                return name1.equals(nameFromId1);
            } else {
                String nameFromId2 = peopleInfo.IdToPerson.get(name2).getFullName();
                return name1.equals(nameFromId2);
            }
        }
    }

    public Person mergePersonToId(Person pName, Person pId) {
        if (pName.getFullName() != null) {
            pId.setFullName(pName.getFullName());
        }
        if (pName.getGender() != null) {
            pId.setGender(pName.getGender());
        }
        for (Map.Entry<String, String> entry : pName.relativeToRole.entrySet()) {
            String name = entry.getKey();
            String role = entry.getValue();
            if (pId.relativeToRole.containsKey(name)) {
                if (Person.isSpecificRole(role)) {
                    pId.relativeToRole.put(name, role);
                }
            }
            pId.relativeToRole.put(name, role);
        }
        return pId;
    }
}
