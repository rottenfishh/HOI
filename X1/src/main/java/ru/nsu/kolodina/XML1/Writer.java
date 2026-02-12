package ru.nsu.kolodina.XML1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class Writer {

    public void writeXML(String path, Map<String, Person> ppl) throws FileNotFoundException, XMLStreamException {
        XMLOutputFactory output = XMLOutputFactory.newInstance();

        XMLStreamWriter writer = output.createXMLStreamWriter(
                new FileOutputStream(path));
        for (Person person: ppl.values()) {
            writer.writeStartElement("person");
            writer.writeAttribute("id", person.id);
            writer.writeStartElement("fullName");
            writer.writeCharacters(person.fullName);
            writer.writeEndElement();

            writer.writeStartElement("gender");
            writer.writeCharacters(person.gender);
            writer.writeEndElement();

            List<PersonToRole> siblings = person.getSiblings();
            if (!siblings.isEmpty()) {
                writer.writeStartElement("siblings");
                for (PersonToRole sibling: siblings) {
                    writer.writeStartElement(sibling.role);
                    writer.writeCharacters(sibling.name);
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }

            List<PersonToRole> children = person.getChildren();
            if (!children.isEmpty()) {
                writer.writeStartElement("children");
                for (PersonToRole child: children) {
                    writer.writeStartElement(child.role);
                    writer.writeCharacters(child.name);
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }

            List<PersonToRole> parents = person.getParents();
            if (!parents.isEmpty()) {
                writer.writeStartElement("parents");
                for (PersonToRole parent: parents) {
                    writer.writeStartElement(parent.role);
                    writer.writeCharacters(parent.name);
                    writer.writeEndElement();
                }
            }

            PersonToRole spouse = person.getSpouce();
            if (spouse != null) {
                writer.writeStartElement(spouse.role);
                writer.writeCharacters(spouse.name);
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
    }

    public void writePrettyXML(String path, Map<String, Person> ppl) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("people");
        root.setAttribute("count", String.valueOf(ppl.size()));
        doc.appendChild(root);
        for (Person person: ppl.values()) {
            Element personElement = doc.createElement("person");
            personElement.setAttribute("id", person.id);

            Element fullName = doc.createElement("fullName");
            fullName.appendChild(doc.createTextNode(person.fullName));
            personElement.appendChild(fullName);

            Element gender = doc.createElement("gender");
            gender.appendChild(doc.createTextNode(person.gender));
            personElement.appendChild(gender);

            List<PersonToRole> siblings = person.getSiblings();
            if (!siblings.isEmpty()) {
                Element siblingsElement = doc.createElement("siblings");
                for (PersonToRole sibling: siblings) {
                    Element sib = doc.createElement(sibling.role);
                    sib.appendChild(doc.createTextNode(sibling.name));
                    siblingsElement.appendChild(sib);
                }
                personElement.appendChild(siblingsElement);
            }

            List<PersonToRole> children = person.getChildren();
            if (!children.isEmpty()) {
                Element childrenElement = doc.createElement("children");
                for (PersonToRole child: children) {
                    Element childElement = doc.createElement(child.role);
                    childElement.appendChild(doc.createTextNode(child.name));
                    childrenElement.appendChild(childElement);
                }
                personElement.appendChild(childrenElement);
            }

            List<PersonToRole> parents = person.getParents();
            if (!parents.isEmpty()) {
                Element parentsElement = doc.createElement("parents");
                for (PersonToRole parent: parents) {
                    Element parentElement = doc.createElement(parent.role);
                    parentElement.appendChild(doc.createTextNode(parent.name));
                    parentsElement.appendChild(parentElement);
                }
                personElement.appendChild(parentsElement);
            }

            PersonToRole spouse = person.getSpouce();
            if (spouse != null) {
                System.out.println(spouse.role);
                Element spouseElement = doc.createElement(spouse.role);
                spouseElement.appendChild(doc.createTextNode(spouse.name));
                personElement.appendChild(spouseElement);
            }
            root.appendChild(personElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // ENABLE INDENTATION
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(path));

        transformer.transform(source, result);
    }
}
