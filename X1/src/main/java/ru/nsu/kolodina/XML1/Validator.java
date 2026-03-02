package ru.nsu.kolodina.XML1;

import lombok.AllArgsConstructor;
import ru.nsu.kolodina.XML1.data.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Validator {

    @AllArgsConstructor
    public static class ValidationError{
        public String personId;
        public String message;
        public String toString() {
            return ("Validation error: " + personId + ": " + message);
        }
    }

    public ValidationError validateAuxMarkers(Person person) {
        if (person.getChildrenNumber() != -1 && person.getChildrenNumber() != person.getChildren().size()) {
            return new ValidationError(person.getId(), "Children marker does not match actual number of children "
                    + "Expected:" + person.getChildrenNumber() + " Actual: " + person.getChildren().size());
        }
        if (person.getSiblingsNumber() != -1 && person.getSiblingsNumber() != person.getSiblings().size()) {
            return new ValidationError(person.getId(), "Siblings marker does not match actual number of siblings "
                  +  "Expected:" + person.getSiblingsNumber() + " Actual: " + person.getSiblings().size());
        }
        return null;
    }

    public List<ValidationError> validatePersons(Map<String, Person> persons) {
        List<ValidationError> report = new ArrayList<>();

        for (Person person : persons.values()) {
            ValidationError error =  validateAuxMarkers(person);
            if (error != null) {
                System.err.println(error);
                report.add(error);
            }
        }
        System.out.println("Report: errors number: " + report.size());
        return report;
    }
}
