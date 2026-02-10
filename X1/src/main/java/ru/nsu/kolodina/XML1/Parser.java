package ru.nsu.kolodina.XML1;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public void readXML(String path) {
        List<Person> people = new ArrayList<Person>();

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
                            String id = element.getAttributeByName(new QName("id")).getValue();
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
                            person.parentIds.add(parent);
                            break;
                        case "spouce":
                            Attribute spouce = element.getAttributeByName(new QName("value"));
                            if (spouce != null) {
                                person.setSpouseValue(spouce.getValue());
                            } else {
                                String value = reader.getElementText();
                                person.setSpouseValue(value);
                            }
                            break;
                        case "siblings":
                            Attribute siblingId = element.getAttributeByName(new QName("val"));
                            if (siblingId != null) {
                                person.siblings.putIfAbsent(siblingId.getValue(), "unknown");
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
                            person.setMother(mother);
                            break;
                        case "father":
                            String father = reader.getElementText();
                            person.setFather(father);
                            break;
                        case "firstname":
                            Attribute firstNameVal = element.getAttributeByName(new QName("value"));
                            if (firstNameVal != null) {
                                person.setFirstName(firstNameVal.getValue());
                            } else {
                                String firstName = reader.getElementText();
                                person.setFirstName(firstName);
                            }
                            break;
                        case "family-name":
                            Attribute familyNameVal = element.getAttributeByName(new QName("value"));
                            if (familyNameVal != null) {
                                person.setLastName(familyNameVal.getValue());
                            } else {
                                String lastName = reader.getElementText();
                                person.setLastName(lastName);
                            }
                            break;
                    }

                    }
                if (event.isEndElement()) {
                    EndElement element = event.asEndElement();
                    String tag = element.getName().getLocalPart();
                    if (tag.equals("person")) {
                        people.add(person);
                    }
                }
            }

        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
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
                    person.siblings.putIfAbsent(name, tag);
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
                String tag = element.getName().getLocalPart();
                Attribute childId = element.getAttributeByName(new QName("id"));
                if (childId != null) { // if it has id in attribute
                    person.children.putIfAbsent(childId.getValue(), tag);
                } else { // if it has name in text
                    String value = reader.getElementText();
                    person.children.putIfAbsent(value, tag);
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
}
