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
        Map<String, List<Person>> IdToPerson = new HashMap<>();
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
                                person.setId(attrId.getValue());
                            }
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
                            person.roleToIDName.putIfAbsent("parent", new ArrayList<>());
                            person.roleToIDName.get("parent").add(parent);
                            break;
                        case "spouce":
                            String spouce = extractFromAttrOrText(reader, element, "value");
                            person.roleToIDName.putIfAbsent("spouce", new ArrayList<>());
                            person.roleToIDName.get("spouce").add(spouce);
                            break;
                        case "siblings":
                            Attribute siblingId = element.getAttributeByName(new QName("val"));
                            if (siblingId != null) {
                                person.roleToIDName.putIfAbsent("siblings", new ArrayList<>());
                                person.roleToIDName.get("siblings").add(siblingId.getValue());
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
                            person.roleToIDName.putIfAbsent("mother", new ArrayList<>());
                            person.roleToIDName.get("mother").add(mother);
                            break;
                        case "father":
                            String father = reader.getElementText();
                            person.roleToIDName.putIfAbsent("father", new ArrayList<>());
                            person.roleToIDName.get("father").add(father);
                            break;
                        case "firstname":
                            String firstName = extractFromAttrOrText(reader, element, "value");
                            person.setFirstName(firstName);
                            break;
                        case "family-name":
                            String familyName = extractFromAttrOrText(reader, element, "value");
                            person.setLastName(familyName);
                            break;
                        case "wife":
                            String wife = extractFromAttrOrText(reader, element, "value");
                            person.roleToIDName.putIfAbsent("wife", new ArrayList<>());
                            person.roleToIDName.get("wife").add(wife);
                            break;
                        case "husband":
                            String husband = extractFromAttrOrText(reader, element, "value");
                            person.roleToIDName.putIfAbsent("husband", new ArrayList<>());
                            person.roleToIDName.get("husband").add(husband);
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
                            peopleInfo.IdToPerson.putIfAbsent(person.id, new ArrayList<>());
                            peopleInfo.IdToPerson.get(person.id).add(person);
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
                if (tag.equals("brother") || tag.equals("sister")) {
                    String name = reader.getElementText();
                    person.roleToIDName.putIfAbsent(tag, new ArrayList<>());
                    person.roleToIDName.get(tag).add(name);
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
                person.roleToIDName.putIfAbsent(tag, new ArrayList<>());

                Attribute childId = element.getAttributeByName(new QName("id"));
                if (childId != null) { // if it has id in attribute
                    person.roleToIDName.get(tag).add(childId.getValue());
                } else { // if it has name in text
                    String value = reader.getElementText();
                    person.roleToIDName.get(tag).add(value);
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
