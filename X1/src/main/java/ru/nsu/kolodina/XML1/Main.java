package ru.nsu.kolodina.XML1;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        PeopleInfo ppl = parser.readXML("people.xml");
        int counter = 0;
        for (Person person : ppl.people) {
            if (person.id == null) {
                counter++;
            }
            if (person.fullName == null) {
                System.out.println("death");
            }
        }
        System.out.println("ppl " +  ppl.people.size());
        System.out.println("ids" + ppl.IdToPerson.size());
        System.out.println("without ids" + counter);
        System.out.println("names" + ppl.NameToPerson.size());

        Collector collector = new Collector(ppl);
        Map<String, Person> result = collector.merge();
        System.out.println(result.containsKey("P410644"));
        Person p = result.get("P410644");
//        for (Map.Entry<String, Person> entry : result.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }
        System.out.println(result.size());
        Writer writer = new Writer();
        try {
            writer.writePrettyXML("output.xml", result);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}