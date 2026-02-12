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

    public String getSpouce() {
        for (Map.Entry<String, String> entry : this.relativeToRole.entrySet()) {
            if (entry.getValue().equals("spouce") || entry.getValue().equals("wife") || entry.getValue().equals("husband")) {
                return entry.getKey();
            }
        }
        return "";
    }

    public List<String> getChildren() {
        List<String> children = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.relativeToRole.entrySet()) {
            if (entry.getValue().equals("child") || entry.getValue().equals("children") || entry.getValue().equals("son") || entry.getValue().equals("daughter")) {
                children.add(entry.getValue());
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

    public List<String> getParents() {
        List<String> parents = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.relativeToRole.entrySet()) {
            if (entry.getValue().equals("parent")) {
                parents.add(entry.getKey());
            }
        }
        return parents;
    }

    public static boolean isSpecificRole(String role) {
        return !(role.equals("spouce") || role.equals("parent") || role.equals("siblings")
                || role.equals("sibling") || role.equals("child"));
    }
}
