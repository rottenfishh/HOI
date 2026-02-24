package ru.nsu.kolodina.XML1;

import generated.People;
import ru.nsu.kolodina.XML1.data.Person;
import ru.nsu.kolodina.XML1.data.PersonToRole;

import java.util.HashMap;
import java.util.Map;

import generated.People;
import ru.nsu.kolodina.XML1.data.Person;
import ru.nsu.kolodina.XML1.data.PersonToRole;

import java.util.*;

public class Converter {

    public static People build(Map<String, Person> persons) {

        People people = new People();
        people.setCount(persons.size());
        Map<String, People.Person> idToJaxb = new HashMap<>();

        for (Person p : persons.values()) {
            People.Person jaxb = new People.Person();
            jaxb.setId(p.getId());
            jaxb.setFullname(p.getFullName());
            if (p.getGender() != null) {
                jaxb.setGender(p.getGender());
            }

            idToJaxb.put(p.getId(), jaxb);
            people.getPerson().add(jaxb);
        }

        for (Person p : persons.values()) {

            People.Person jaxb = idToJaxb.get(p.getId());

            List<PersonToRole> parentsList = p.getParents();
            if (!parentsList.isEmpty()) {
                People.Person.Parents parents = new People.Person.Parents();

                for (PersonToRole rel : parentsList) {
                    People.Person parentJaxb = idToJaxb.get(rel.getName());
                    if (parentJaxb == null) continue;

                    switch (rel.getRole()) {
                        case "mother":
                            parents.setMother(parentJaxb);
                            break;
                        case "father":
                            parents.setFather(parentJaxb);
                            break;
                        default:
                            parents.setParent(parentJaxb);
                    }
                }

                jaxb.setParents(parents);
            }

            List<PersonToRole> childrenList = p.getChildren();
            if (!childrenList.isEmpty()) {
                People.Person.Children children = new People.Person.Children();

                for (PersonToRole rel : childrenList) {
                    People.Person childJaxb = idToJaxb.get(rel.getName());
                    if (childJaxb == null) continue;

                    switch (rel.getRole()) {
                        case "son":
                            children.setSon(childJaxb);
                            break;
                        case "daughter":
                            children.setDaugther(childJaxb);
                            break;
                        default:
                            children.setChild(childJaxb);
                    }
                }

                jaxb.setChildren(children);
            }

            List<PersonToRole> siblingsList = p.getSiblings();
            if (!siblingsList.isEmpty()) {
                People.Person.Siblings siblings = new People.Person.Siblings();

                for (PersonToRole rel : siblingsList) {
                    People.Person sibJaxb = idToJaxb.get(rel.getName());
                    if (sibJaxb == null) continue;

                    switch (rel.getRole()) {
                        case "brother":
                            siblings.setBrother(sibJaxb);
                            break;
                        case "sister":
                            siblings.setSister(sibJaxb);
                            break;
                        default:
                            siblings.setSiblings(sibJaxb);
                    }
                }

                jaxb.setSiblings(siblings);
            }

            PersonToRole spouse = p.getSpouce();
            if (spouse != null) {
                People.Person spouseJaxb = idToJaxb.get(spouse.getName());
                if (spouseJaxb != null) {
                    switch (spouse.getRole()) {
                        case "wife":
                            jaxb.setWife(spouseJaxb);
                            break;
                        case "husband":
                            jaxb.setHusband(spouseJaxb);
                            break;
                        default:
                            jaxb.setSpouce(spouseJaxb);
                    }
                }
            }
        }

        return people;
    }
}
