package ctfmodell.serialization;

import com.sun.xml.internal.stream.XMLOutputFactoryImpl;
import ctfmodell.model.Landscape;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ctfmodell.Main.XML_FOLDER;

public class XMLSerialization {


    public void save(Landscape landscape, String name) {
        try {
            Path directory = Paths.get(XML_FOLDER, name + ".txml");
            File xmlFile = directory.toFile();

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(new FileOutputStream(xmlFile), "UTF-8");
            writer.writeStartDocument("utf-8", "1.0");
            writer.writeCharacters("\n");
            writer.writeDTD("<!DOCTYPE landscape " +
                        "[\n" +
                            "<!ELEMENT landscape (#PCDATA) (field+,officer)>\n" +
                            "<!ELEMENT field (#PCDATA)>\n" +
                            "<!ELEMENT officer (#PCDATA)>\n" +
                            "\n" +
                            "<!ATTLIST field y CDATA #REQUIRED>\n" +
                            "<!ATTLIST field x CDATA #REQUIRED>\n" +
                            "<!ATTLIST field type CDATA #REQUIRED>\n" +
                            "\n" +
                            "<!ATTLIST officer direction CDATA #REQUIRED>\n" +
                            "<!ATTLIST officer numberOfFlags CDATA #REQUIRED>\n" +
                            "]"+
                    ">");
            writer.writeCharacters("\n");

            //Schreibe alle Landschaft Tags
            writer.writeStartElement("landscape");
            writer.writeCharacters("\n");
            writer.writeEndElement(); // landscape

            //Ende des Dokuments
            writer.writeEndDocument();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {

    }

}
