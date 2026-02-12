package ru.nsu.kolodina.XML1;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import java.io.FileInputStream;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        PeopleInfo ppl = parser.readXML("people.xml");
        for (Person person : ppl.people) {
            System.out.println(person.id);
        }
    }
}