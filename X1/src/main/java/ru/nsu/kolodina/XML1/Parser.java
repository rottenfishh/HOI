package ru.nsu.kolodina.XML1;

import lombok.AllArgsConstructor;

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

@AllArgsConstructor
public class Parser {

    public PeopleInfo readXML(String path) {
        List<Person> people = new ArrayList<Person>();
        Map<String, Person> IdToPerson = new HashMap<>();
        Map<String, List<Person>> NameToPerson = new HashMap<>();
        PeopleInfo peopleInfo = new PeopleInfo(IdToPerson, NameToPerson, people);

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        InputStream fis = getClass().getClassLoader().getResourceAsStream(path);
        XMLEventReader reader = null;
        try {
            reader = xmlInputFactory.createXMLEventReader(fis);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        try {
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
                                    person = IdToPerson.get(attrId.getValue());
                                }
                                person.setId(attrId.getValue());
                            }
                            break;
                        case "id":
                            String id = extractFromAttrOrText(reader, element, "value");
                            if (IdToPerson.containsKey(id)) {
                                person = IdToPerson.get(id); // fix later idk. merge them
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
                            String parent = extractFromAttrOrText(reader, element, "value");
                            if (!person.relativeToRole.containsKey(parent)) { // if person with relative role is not here yet, put it.
                                person.relativeToRole.put(parent, "parent"); //if it is, their role cannot be more specific than one already here
                            }
                            break;
                        case "spouce":
                            String spouce = extractFromAttrOrText(reader, element, "value");
                            if (!person.relativeToRole.containsKey(spouce)) {
                                person.relativeToRole.put(spouce, "spouce");
                            }
                            break;
                        case "siblings":
                            Attribute siblingId = element.getAttributeByName(new QName("val"));
                            if (siblingId != null) {
                                if (!person.relativeToRole.containsKey(siblingId.getValue())) {
                                    person.relativeToRole.put(siblingId.getValue(), "siblings");
                                }
                            } else {
                                parseSiblings(reader, person);
                            }
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
                            String mother = reader.getElementText();
                            person.relativeToRole.put(mother, "mother");
                            break;
                        case "father":
                            String father = reader.getElementText();
                            person.relativeToRole.put(father, "father");
                            break;
                        case "firstname":
                            String firstName = extractFromAttrOrText(reader, element, "value");
                            person.setFirstName(firstName);
                            break;
                        case "surname":
                            String surname = extractFromAttrOrText(reader, element, "value");
                            person.setLastName(surname);
                            break;
                        case "family-name":
                            String familyName = extractFromAttrOrText(reader, element, "value");
                            person.setLastName(familyName);
                            break;
                        case "wife":
                            String wife = extractFromAttrOrText(reader, element, "value");
                            person.relativeToRole.put(wife, "wife");
                            break;
                        case "husband":
                            String husband = extractFromAttrOrText(reader, element, "value");
                            person.relativeToRole.put(husband, "husband");
                            break;
                    }

                    }
                if (event.isEndElement()) {
                    EndElement element = event.asEndElement();
                    String tag = element.getName().getLocalPart();
                    if (tag.equals("person")) {
                        peopleInfo.people.add(person);
                        collectFullName(person);

                        if (!Objects.equals(person.id, "")) {
                            peopleInfo.IdToPerson.put(person.id, person);
                        }

                        if (!Objects.equals(person.fullName, "")) {
                            peopleInfo.NameToPerson.putIfAbsent(person.fullName, new ArrayList<>());
                            peopleInfo.NameToPerson.get(person.fullName).add(person);
                        }
                    }
                }
            }

        } catch (XMLStreamException e) {
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

    //surname
    public void parseFullName(XMLEventReader reader, Person person) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {
                String tag = nextEvent.asStartElement().getName().getLocalPart();
                switch (tag) {
                    case "first":
                        person.setFirstName(reader.getElementText());
                        break;
                    case "family":
                        person.setLastName(reader.getElementText());
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
                    person.relativeToRole.put(name, tag);
                }
                if (tag.equals("child")){
                    if (!person.relativeToRole.containsKey(name)) {
                        person.relativeToRole.put(name, tag);
                    }
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
                if (childId != null) { // if it has id in attribute
                    person.relativeToRole.put(childId.getValue(), tag);
                } else { // if it has name in text
                    String value = reader.getElementText();
                    person.relativeToRole.put(value, tag);
                }
            }

            if (nextEvent.isEndElement()) {
                String tag = nextEvent.asEndElement().getName().getLocalPart();
                if (tag.equals("children")) {
                    return;
                }
            }
        }

    }

    public void collectFullName(Person person) {
        if (person.fullName != null) {
            return;
        }
        StringBuilder name = new StringBuilder();
        if (person.firstName != null) {
            name.append(person.firstName);
        }
        if (person.lastName != null) {
            name.append(" ").append(person.lastName);
        }
        person.fullName = name.toString();
    }
}
