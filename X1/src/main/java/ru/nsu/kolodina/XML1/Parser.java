package ru.nsu.kolodina.XML1;

import lombok.AllArgsConstructor;
import ru.nsu.kolodina.XML1.data.PeopleInfo;
import ru.nsu.kolodina.XML1.data.Person;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.*;

public class Parser {

    @AllArgsConstructor
    public static class ParserError {
        public String message;
        public String error;
        public String toString() {
            return ("Parser error: " + ": " + error + "msg: " + message);
        }
    }

    public PeopleInfo readXML(String path) {
        List<Person> people = new ArrayList<>();
        Map<String, Person> IdToPerson = new HashMap<>();
        Map<String, List<Person>> NameToPerson = new HashMap<>();
        PeopleInfo peopleInfo = new PeopleInfo(IdToPerson, NameToPerson, people);

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        InputStream fis = getClass().getClassLoader().getResourceAsStream(path);
        try {
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(fis);

            Person person = new Person();
            while (reader.hasNext()) {
                XMLEvent event = null;
                event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement element = event.asStartElement();
                    switch (element.getName().getLocalPart()) {
                        case "person":
                            person = new Person();
                            Attribute  attrId = element.getAttributeByName(new QName("id"));
                            if (attrId != null) {
                                if (IdToPerson.containsKey(attrId.getValue())) {
                                    person = Collector.mergePersonToId(IdToPerson.get(attrId.getValue()), person);
                                    //person = IdToPerson.get(attrId.getValue());
                                }
                                person.setId(trimName(attrId.getValue()));
                            }

                            Attribute nameId = element.getAttributeByName(new QName("name"));
                            if (nameId != null) {
                                person.setFullName(trimName(nameId.getValue()));
                            }
                            break;
                        case "id":
                            String id = extractFromAttrOrText(reader, element, "value").trim();
                            if (IdToPerson.containsKey(id)) {
                                person = Collector.mergePersonToId(IdToPerson.get(id), person);
                               // person = IdToPerson.get(id); // fix later idk. merge them
                            }
                            person.setId(id);
                            break;
                        case "fullname":
                            parseFullName(reader, person);
                            break;
                        case "gender":
                            String gender = extractFromAttrOrText(reader, element, "value");
                            person.setGender(gender);
                            break;
                        case "parent":
                            String parent = trimName(extractFromAttrOrText(reader, element, "value"));
                            if (!person.getRelativeToRole().containsKey(parent)) { // if person with relative role is not here yet, put it.
                                person.getRelativeToRole().put(parent, "parent"); //if it is, their role cannot be more specific than one already here
                            }
                            break;
                        case "spouce":
                            String spouce = trimName(extractFromAttrOrText(reader, element, "value"));

                            if (!person.getRelativeToRole().containsKey(spouce)) {
                                person.getRelativeToRole().put(spouce, "spouce");
                            }
                            break;
                        case "siblings":
                            Attribute siblingId = element.getAttributeByName(new QName("val"));
                            if (siblingId != null) {
                                String[] sibs = siblingId.getValue().split(" ");
                                for (String sib : sibs) {
                                    if (!person.getRelativeToRole().containsKey(sib)) {
                                        person.getRelativeToRole().put(trimName(sib), "siblings");
                                    }
                                }
                            } else {
                                parseSiblings(reader, person);
                            }
                            break;
                        case "siblings-number":
                            Attribute siblingNumber = element.getAttributeByName(new QName("value"));
                            int numberSibs = Integer.parseInt(siblingNumber.getValue());
                            person.setSiblingsNumber(numberSibs);
                            break;
                        case "children":
                            parseChildren(reader, person);
                            break;
                        case "children-number":
                            Attribute value = element.getAttributeByName(new QName("value"));
                            int number = Integer.parseInt(value.getValue());
                            person.setChildrenNumber(number);
                            break;
                        case "mother":
                            String mother = trimName(reader.getElementText());
                            person.getRelativeToRole().put(mother, "mother");
                            break;
                        case "father":
                            String father = trimName(reader.getElementText());
                            person.getRelativeToRole().put(father, "father");
                            break;
                        case "firstname":
                            String firstName = trimName(extractFromAttrOrText(reader, element, "value"));
                            person.setFirstName(firstName);
                            break;
                        case "surname":
                            String surname = trimName(extractFromAttrOrText(reader, element, "value"));
                            person.setLastName(surname);
                            break;
                        case "family-name":
                            String familyName = trimName(extractFromAttrOrText(reader, element, "value"));
                            person.setLastName(familyName);
                            break;
                        case "wife":
                            String wife = trimName(extractFromAttrOrText(reader, element, "value"));
                            person.getRelativeToRole().put(wife, "wife");
                            break;
                        case "husband":
                            String husband = trimName(extractFromAttrOrText(reader, element, "value"));
                            person.getRelativeToRole().put(husband, "husband");
                            break;
                    }
                }
                if (event.isEndElement()) {
                    EndElement element = event.asEndElement();
                    String tag = element.getName().getLocalPart();
                    if (tag.equals("person")) {
                        peopleInfo.getPeople().add(person);
                        if (person.getFullName() == null || person.getFullName().isEmpty()) {
                            collectFullName(person);
                        }
                        if (!Objects.equals(person.getId(), null)) {
                            peopleInfo.getIdToPerson().put(person.getId(), person);
                        } else { // put to names if only no id
                            //System.out.println(person.fullName);
                            if (!Objects.equals(person.getFullName(), null)) {
                                peopleInfo.getNameToPerson().putIfAbsent(person.getFullName(), new ArrayList<>());
                                peopleInfo.getNameToPerson().get(person.getFullName()).add(person);
                            }
                        }
                    }
                }
            }

        } catch (XMLStreamException e) {
            ParserError error = new ParserError(e.getMessage(), e.toString());
            System.err.println(error);
            throw new RuntimeException(e);
        }
        return peopleInfo;
    }

    public String extractFromAttrOrText(XMLEventReader reader, StartElement element, String attrName) throws XMLStreamException {
        String val = "unknown";
        Attribute attrVal = element.getAttributeByName(new QName(attrName));
        if (attrVal != null) {
            val = attrVal.getValue();
        } else {
            val = reader.getElementText();
        }
        return val;
    }

    public void parseFullName(XMLEventReader reader, Person person) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {
                String tag = nextEvent.asStartElement().getName().getLocalPart();
                switch (tag) {
                    case "first":
                        person.setFirstName(reader.getElementText().trim());
                        break;
                    case "family":
                        person.setLastName(reader.getElementText().trim());
                }
            }
            if (nextEvent.isEndElement()) {
                String tag = nextEvent.asEndElement().getName().getLocalPart();
                if (tag.equals("fullname")) {
                    return;
                }
            }
        }
    }

    public void parseSiblings(XMLEventReader reader, Person person) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {
                StartElement element = nextEvent.asStartElement();
                String tag = element.getName().getLocalPart();
                String name = reader.getElementText();
                if (tag.equals("brother") || tag.equals("sister")) {
                    person.getRelativeToRole().put(trimName(name), tag);
                }
            }

            if (nextEvent.isEndElement()) {
                String tag = nextEvent.asEndElement().getName().getLocalPart();
                if (tag.equals("siblings")) {
                    return;
                }
            }
        }
    }

    public void parseChildren(XMLEventReader reader, Person person) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {
                StartElement element = nextEvent.asStartElement();
                String tag = element.getName().getLocalPart(); // child role
                Attribute childId = element.getAttributeByName(new QName("id"));
                String childName;
                if (childId != null) {
                    childName = childId.getValue();
                } else {
                    childName = reader.getElementText();
                }
                person.getRelativeToRole().put(trimName(childName), tag);
            }

            if (nextEvent.isEndElement()) {
                String tag = nextEvent.asEndElement().getName().getLocalPart();
                if (tag.equals("children")) {
                    return;
                }
            }
        }

    }

    public static void collectFullName(Person person) {
        String first = person.getFirstName() != null ? person.getFirstName() : "";
        String last = person.getLastName() != null ? person.getLastName() : "";

        if (!first.isEmpty() && !last.isEmpty()) {
            person.setFullName(first + " " + last);
        } else if (!first.isEmpty()) {
            person.setFullName(first);
        } else if (!last.isEmpty()) {
            person.setFullName(last);
        } else {
            person.setFullName("");
        }
    }

    public static String trimName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }
}
