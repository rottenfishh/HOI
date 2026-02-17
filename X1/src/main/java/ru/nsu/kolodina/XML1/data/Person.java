package ru.nsu.kolodina.XML1.data;

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
    int siblingsNumber;

    public Person() {
        this.relativeToRole = new HashMap<>();
        this.childrenNumber = -1;
        this.siblingsNumber = -1;
    }

    public PersonToRole getSpouce() {
        for (Map.Entry<String, String> entry : this.relativeToRole.entrySet()) {
            if (entry.getValue().equals("spouce") || entry.getValue().equals("wife") || entry.getValue().equals("husband")) {
                return new PersonToRole(entry.getKey(), entry.getValue());
            }
        }
        return null;
    }

    public List<PersonToRole> getChildren() {
        List<PersonToRole> children = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.relativeToRole.entrySet()) {
            if (entry.getValue().equals("child") || entry.getValue().equals("children") || entry.getValue().equals("son") || entry.getValue().equals("daughter")) {
                PersonToRole h = new PersonToRole(entry.getKey(), entry.getValue());
                children.add(h);
            }
        }
        return children;
    }

    public String getMother() {
        for (Map.Entry<String, String> entry : this.relativeToRole.entrySet()) {
            if (entry.getValue().equals("mother")) {
                return entry.getKey();
            }
        }
        return "";
    }
    public String getFather() {
        for (Map.Entry<String, String> entry : this.relativeToRole.entrySet()) {
            if (entry.getValue().equals("father")) {
                return entry.getKey();
            }
        }
        return "";
    }

    public List<PersonToRole> getParents() {
        List<PersonToRole> parents = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.relativeToRole.entrySet()) {
            if (entry.getValue().equals("parent") || entry.getValue().equals("mother") || entry.getValue().equals("father")) {
                PersonToRole h = new PersonToRole(entry.getKey(), entry.getValue());
                parents.add(h);
            }
        }
        return parents;
    }

    public List<PersonToRole> getSiblings() {
        List<PersonToRole> siblings = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.relativeToRole.entrySet()) {
            if (entry.getValue().equals("sibling") || entry.getValue().equals("siblings")
                    || entry.getValue().equals("brother") || entry.getValue().equals("sister")) {
                PersonToRole h = new PersonToRole(entry.getKey(), entry.getValue());
                siblings.add(h);
            }
        }
        return siblings;
    }
    public static boolean isSpecificRole(String role) {
        return !(role.equals("spouce") || role.equals("parent") || role.equals("siblings")
                || role.equals("sibling") || role.equals("child"));
    }

    public static boolean isId(String name) {
        if (name.startsWith("P") && Character.isDigit(name.charAt(1))) {
            return true;
        }
        return false;
    }
}
