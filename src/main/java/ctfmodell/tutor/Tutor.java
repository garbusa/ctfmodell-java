package ctfmodell.tutor;

import ctfmodell.controller.Controller;
import ctfmodell.model.StudentExample;
import ctfmodell.serialization.XMLSerialization;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.rmi.registry.Registry;

public class Tutor {

    public static TutorialSystem tutorialSystem;
    public static Registry registry;

    static void loadStudentExample(Controller controller, StudentExample example) {
        controller.getCodeEditor().setText(example.getCode());

        Reader r = new StringReader(example.getXml());
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser;

        try {
            parser = factory.createXMLStreamReader(r);
            XMLSerialization.loadXML(parser, controller);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    static void saveStudentExample(StudentExample example) {
        ((TutorialSystemImpl) tutorialSystem).saveAnswer(example);
    }

}
