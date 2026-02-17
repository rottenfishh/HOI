package ru.nsu.kolodina.XML1;

import ru.nsu.kolodina.XML1.data.PeopleInfo;
import ru.nsu.kolodina.XML1.data.Person;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        PeopleInfo ppl = parser.readXML("people.xml");

        Collector collector = new Collector(ppl);
        Map<String, Person> result = collector.merge();
        System.out.println("Report: people number: " + result.size());

        Validator validator = new Validator();
        validator.validatePersons(result);

        Writer writer = new Writer();
        try {
            writer.writePrettyXML("output2.xml", result);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}