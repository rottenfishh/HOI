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
        int counter = 0;
        for (Person person : ppl.getPeople()) {
            if (person.getId() == null) {
                counter++;
            }
            if (person.getFullName() == null) {
                System.out.println("death");
            }
        }
        System.out.println("ppl " +  ppl.getPeople().size());
        System.out.println("ids" + ppl.getIdToPerson().size());
        System.out.println("without ids" + counter);
        System.out.println("names" + ppl.getNameToPerson().size());

        Collector collector = new Collector(ppl);
        Map<String, Person> result = collector.merge();
        System.out.println(result.size());

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