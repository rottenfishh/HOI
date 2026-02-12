package ru.nsu.kolodina.XML1;

import lombok.AllArgsConstructor;

import java.util.*;

@AllArgsConstructor
public class Collector {
    PeopleInfo peopleInfo;

    public Map<String, Person> merge() {
        Map<String, Person> idToPerson = new HashMap<>();
        System.out.println("yeah" + peopleInfo.getIdToPerson().containsKey("P410644"));
        for (Map.Entry<String, Person> entry : peopleInfo.IdToPerson.entrySet()) {
            Person fullPerson = entry.getValue();
            Map<String, String> relatives = new HashMap<>();
            List<Person> personFromName = peopleInfo.getNameToPerson().get(fullPerson.getFullName());
//            for (Person person : personFromName) { // if we merged this person by id alone, remove it from names list
//                if (person.getId().equals(fullPerson.getId())) { // if it does have id, its already merged
//                    peopleInfo.getNameToPerson().get(fullPerson.getFullName()); // dont remove this
//                }
//            }
            if (peopleInfo.getNameToPerson().containsKey(fullPerson.getFullName())) {
                peopleInfo.getNameToPerson().get(fullPerson.getFullName()).add(fullPerson); // put full person idk
            }

            if (idToPerson.containsKey(entry.getKey())) {
                System.out.println("duplicate id: " + entry.getKey());
            }
            idToPerson.put(entry.getKey(), fullPerson);
        }
        System.out.println("yo" + idToPerson.containsKey("P410644"));
        mergePeopleWithOnlyName(idToPerson, peopleInfo.getNameToPerson(), peopleInfo.getPeople());

        for (Person guy: idToPerson.values()) {
            Parser.collectFullName(guy);
            mergeRelativesByIds(guy, peopleInfo.getNameToPerson(), idToPerson);
        }

        System.out.println("mm" + idToPerson.containsKey("P410644"));
        return idToPerson;
    }

    public Map<String, String> mergeRelativesByIds(Person person, Map<String, List<Person>> nameToPerson, Map<String, Person> idToPerson) {
        List<String> namesToRemove = new ArrayList<>();
        for (Map.Entry<String, String> relative: person.relativeToRole.entrySet()) { // merge relatives by id
            if (Person.isId(relative.getKey())) {
                if (!idToPerson.containsKey(relative.getKey())) {
                    System.out.println("why" + relative.getKey() + " doesn't exist");
                }
                String name = idToPerson.get(relative.getKey()).getFullName();
                String role = relative.getValue();
                if (!Person.isSpecificRole(role)) {
                    if (name != null) {
                        String relativeRole = person.relativeToRole.get(name);
                        if (relativeRole != null) {
                            person.relativeToRole.put(relative.getKey(), relativeRole);
                        }
                    }
                }
                // if we already have relative id here, delete their name
//                person.relativeToRole.remove(name);
                namesToRemove.add(name);
            }
        }

        List<PersonToRole> idsToPut = new ArrayList<>();
        for (Map.Entry<String, String> relative: person.relativeToRole.entrySet()) {
            if (!Person.isId(relative.getKey())) {
                // find their ids by name
                List<Person> suspects = nameToPerson.get(relative.getKey()); // нашли потенциальных родственников по имени
                if (suspects == null) {
                    continue;
                }
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
                    //person.relativeToRole.put(dude.getId(), role);
                    idsToPut.add(new PersonToRole(dude.getId(), role));
                }
//                person.relativeToRole.remove(relative.getKey());
                namesToRemove.add(relative.getKey());
            }
        }
        for (String name: namesToRemove) {
            person.relativeToRole.remove(name);
        }
        for (PersonToRole id: idsToPut) {
            person.relativeToRole.put(id.name, id.role);
        }
        return person.relativeToRole;
    }

    public void mergePeopleWithOnlyName(Map<String, Person> idToPerson, Map<String, List<Person>> nameToPerson, List<Person> people) {
        // используя вторичные признаки??? kill myself
        System.out.println(nameToPerson.containsKey("Shawna Nine"));
        for (Map.Entry<String, List<Person>> entry : nameToPerson.entrySet()) {
            if (entry.getKey().equals("Shawna Nine")) {
                System.out.println("ye" + entry.getValue().size());
            }
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
                }
                for (Person id: ids) {
                    int childNum1 = person.getChildrenNumber();
                    int childNum2 = id.getChildrenNumber();
                    if (childNum1 != 0 && childNum2 != 0) {
                        if (childNum1 != childNum2) {
                            continue;
                        }
                    }
                    if ((person.getGender() != null) && !(person.getGender().equals(id.getGender()))) {
                        continue;
                    }
                    if (!validateRelatives(person, id)) {
                        continue;
                    }
                    Person fullPerson = mergePersonToId(person, id);
                    Parser.collectFullName(fullPerson);
                    idToPerson.put(fullPerson.id, fullPerson);
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
        if (!Objects.equals(person1.getSpouce(), null) && !Objects.equals(person2.getSpouce(), null)) {
            String spouse1 = person1.getSpouce().name;
            String spouse2 = person2.getSpouce().name;
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
            // idk find mother name. check if its the same person
            if (Person.isId(name1)) {
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
