package ru.nsu.kolodina.XML1;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import java.io.FileInputStream;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        PeopleInfo ppl = parser.readXML("people.xml");
        int counter = 0;
        for (Person person : ppl.people) {
            if (person.id == null) {
                counter++;
            }
            System.out.println(person.id);
        }
        System.out.println("ppl " +  ppl.people.size());
        System.out.println("ids" + ppl.IdToPerson.size());
        System.out.println("without ids" + counter);
    }
}