package ru.nsu.kolodina.XML1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.nsu.kolodina.XML1.data.Person;
import ru.nsu.kolodina.XML1.data.PersonToRole;

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

    public void writePrettyXML(String path, Map<String, Person> ppl) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("people");
        root.setAttribute("count", String.valueOf(ppl.size()));
        doc.appendChild(root);
        for (Person person: ppl.values()) {
            Element personElement = doc.createElement("person");
            personElement.setAttribute("id", person.getId());

            Element fullName = doc.createElement("fullName");
            fullName.appendChild(doc.createTextNode(person.getFullName()));
            personElement.appendChild(fullName);

            Element gender = doc.createElement("gender");
            gender.appendChild(doc.createTextNode(person.getGender()));
            personElement.appendChild(gender);

            List<PersonToRole> siblings = person.getSiblings();
            if (!siblings.isEmpty()) {
                Element siblingsElement = doc.createElement("siblings");
                for (PersonToRole sibling: siblings) {
                    Element sib = doc.createElement(sibling.getRole());
                    sib.appendChild(doc.createTextNode(sibling.getName()));
                    siblingsElement.appendChild(sib);
                }
                personElement.appendChild(siblingsElement);
            }

            List<PersonToRole> children = person.getChildren();
            if (!children.isEmpty()) {
                Element childrenElement = doc.createElement("children");
                for (PersonToRole child: children) {
                    Element childElement = doc.createElement(child.getRole());
                    childElement.appendChild(doc.createTextNode(child.getName()));
                    childrenElement.appendChild(childElement);
                }
                personElement.appendChild(childrenElement);
            }

            List<PersonToRole> parents = person.getParents();
            if (!parents.isEmpty()) {
                Element parentsElement = doc.createElement("parents");
                for (PersonToRole parent: parents) {
                    Element parentElement = doc.createElement(parent.getRole());
                    parentElement.appendChild(doc.createTextNode(parent.getName()));
                    parentsElement.appendChild(parentElement);
                }
                personElement.appendChild(parentsElement);
            }

            PersonToRole spouse = person.getSpouce();
            if (spouse != null) {
                Element spouseElement = doc.createElement(spouse.getRole());
                spouseElement.appendChild(doc.createTextNode(spouse.getName()));
                personElement.appendChild(spouseElement);
            }
            root.appendChild(personElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(path));

        transformer.transform(source, result);
    }
}
