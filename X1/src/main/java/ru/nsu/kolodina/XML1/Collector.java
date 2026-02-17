package ru.nsu.kolodina.XML1;

import lombok.AllArgsConstructor;
import ru.nsu.kolodina.XML1.data.PeopleInfo;
import ru.nsu.kolodina.XML1.data.Person;
import ru.nsu.kolodina.XML1.data.PersonToRole;

import java.util.*;

@AllArgsConstructor
public class Collector {
    PeopleInfo peopleInfo;

    public Map<String, Person> merge() {
        Map<String, Person> idToPerson = new HashMap<>();
        System.out.println("yeah" + peopleInfo.getIdToPerson().containsKey("P410644"));
        for (Map.Entry<String, Person> entry : peopleInfo.getIdToPerson().entrySet()) {
            Person fullPerson = entry.getValue();
            Map<String, String> relatives = new HashMap<>();

            if (peopleInfo.getNameToPerson().containsKey(fullPerson.getFullName())) {
                peopleInfo.getNameToPerson().get(fullPerson.getFullName()).add(fullPerson); // put full person idk
            }

            if (idToPerson.containsKey(entry.getKey())) {
                System.out.println("duplicate id: " + entry.getKey());
            }
            idToPerson.put(entry.getKey(), fullPerson);
        }

        for (Person guy: peopleInfo.getPeople()) {
            Parser.collectFullName(guy);
            mergeRelativesByIds(guy, peopleInfo.getNameToPerson(), idToPerson);
        }
        mergePeopleWithOnlyName(idToPerson, peopleInfo.getNameToPerson(), peopleInfo.getPeople());
        for (Person guy : idToPerson.values()) {
            mergeRelativesByIds(
                    guy,
                    peopleInfo.getNameToPerson(),
                    idToPerson
            );
        }
        System.out.println("mm" + idToPerson.containsKey("P410644"));
        return idToPerson;
    }

    public Map<String, String> mergeRelativesByIds(Person person, Map<String, List<Person>> nameToPerson, Map<String, Person> idToPerson) {
        List<String> namesToRemove = new ArrayList<>();
        for (Map.Entry<String, String> relative: person.getRelativeToRole().entrySet()) {
            if (Person.isId(relative.getKey())) {
                if (!idToPerson.containsKey(relative.getKey())) {
                    System.out.println("why" + relative.getKey() + " doesn't exist");
                }
                String name = idToPerson.get(relative.getKey()).getFullName();
                String role = relative.getValue();
                if (!Person.isSpecificRole(role)) {
                    if (name != null) {
                        String relativeRole = person.getRelativeToRole().get(name);
                        if (relativeRole != null) {
                            person.getRelativeToRole().put(relative.getKey(), relativeRole);
                        }
                    }
                }
                namesToRemove.add(name);
            }
        }

        List<PersonToRole> idsToPut = new ArrayList<>();
        for (Map.Entry<String, String> relative: person.getRelativeToRole().entrySet()) {
            if (!Person.isId(relative.getKey())) {
                List<Person> suspects = nameToPerson.get(relative.getKey());
                if (suspects == null) {
                    continue;
                }
                for (Person dude : suspects) {
                    if (dude.getId() == null) {
                        continue;
                    }
                    String role = relative.getValue();

                    String ourDudeRole = dude.getRelativeToRole().get(person.getId());
                    if (ourDudeRole == null) {
                        ourDudeRole = dude.getRelativeToRole().get(person.getFullName());
                    }
                    if (ourDudeRole == null) {
                        continue;
                    }
                    idsToPut.add(new PersonToRole(dude.getId(), role));
                }
                namesToRemove.add(relative.getKey());
            }
        }
        for (String name: namesToRemove) {
            person.getRelativeToRole().remove(name);
        }
        for (PersonToRole id: idsToPut) {
            person.getRelativeToRole().put(id.getName(), id.getRole());
        }
        return person.getRelativeToRole();
    }

    public void mergePeopleWithOnlyName(Map<String, Person> idToPerson, Map<String, List<Person>> nameToPerson, List<Person> people) {
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
                if (ids.size() == 1) {
                    Person fullPerson = mergePersonToId(person, ids.getFirst());
                    idToPerson.put(fullPerson.getId(), fullPerson);
                    continue;
                }
                for (Person id: ids) {
                    if (!checkAuxMarkersAndGender(person, id)) {
                        continue;
                    }
                    if (!validateRelatives(person, id)) {
                        continue;
                    }
                    Person fullPerson = mergePersonToId(person, id);
                    Parser.collectFullName(fullPerson);
                    idToPerson.put(fullPerson.getId(), fullPerson);
                }
            }
        }
    }

    public boolean checkAuxMarkersAndGender(Person person1, Person person2) {
        int childNum1 = person1.getChildrenNumber();
        int childNum2 = person2.getChildrenNumber();
        if (childNum1 != -1 && childNum2 != -1) {
            if (childNum1 != childNum2) {
                return false;
            }
        }

        int sibNum1 = person1.getSiblingsNumber();
        int sibNum2 = person2.getSiblingsNumber();
        if (sibNum1 != -1 && sibNum2 != -1) {
            if (sibNum1 != sibNum2) {
                return false;
            }
        }

        if ((person1.getGender() != null) && (person2.getGender() != null)) {
            Character g1 = person1.getGender().toLowerCase().charAt(0);
            Character g2 = person2.getGender().toLowerCase().charAt(0);
            return g1.equals(g2);
        }
        return true;
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
        if (!Objects.equals(person1.getSpouce(), null) && !Objects.equals(person2.getSpouce(), null)) {
            String spouse1 = person1.getSpouce().getName();
            String spouse2 = person2.getSpouce().getName();
            if (!isSamePerson(spouse1, spouse2)) {
                return false;
            }
        }
        return true;
    }

    public boolean isSamePerson(String name1, String name2) {
        if (Person.isId(name1) && Person.isId(name2)) {
            return name1.equals(name2);
        }
        else if (!Person.isId(name1) && !Person.isId(name2)) {
            return name1.equals(name2);
        } else {
            if (Person.isId(name1)) {
                String nameFromId1 = peopleInfo.getIdToPerson().get(name1).getFullName();
                return nameFromId1.equals(name2);
            } else {
                String nameFromId2 = peopleInfo.getIdToPerson().get(name2).getFullName();
                return nameFromId2.equals(name1);
            }
        }
    }

    public static Person mergePersonToId(Person pName, Person pId) {
        if (pName.getFullName() != null) {
            pId.setFullName(pName.getFullName());
        }
        if (pName.getGender() != null) {
            pId.setGender(pName.getGender());
        }
        for (Map.Entry<String, String> entry : pName.getRelativeToRole().entrySet()) {
            String name = entry.getKey();
            String role = entry.getValue();
            if (pId.getRelativeToRole().containsKey(name)) {
                if (Person.isSpecificRole(role)) {
                    pId.getRelativeToRole().put(name, role);
                }
            }else {
                pId.getRelativeToRole().put(name, role);
            }
        }
        return pId;
    }
}
