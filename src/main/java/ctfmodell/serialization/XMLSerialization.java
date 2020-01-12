package ctfmodell.serialization;

import ctfmodell.controller.Controller;
import ctfmodell.model.Landscape;
import ctfmodell.model.PoliceOfficer;
import ctfmodell.model.enums.Direction;
import ctfmodell.model.enums.Field;
import ctfmodell.provider.DialogProvider;
import ctfmodell.util.Coordinates;
import javafx.scene.control.Alert;

import javax.xml.stream.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static ctfmodell.Main.XML_FOLDER;

public class XMLSerialization {


    public static void save(Landscape landscape, String name) {
        try {
            Path directory = Paths.get(XML_FOLDER, name + ".txml");
            File xmlFile = directory.toFile();

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(new FileOutputStream(xmlFile), "UTF-8");
            writeXML(writer, landscape);
            DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Erfolgreich", "Speichern", "Landschaft wurde gespeichert!");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void writeXML(XMLStreamWriter writer, Landscape landscape) throws XMLStreamException {
        writer.writeStartDocument("utf-8", "1.0");
        writer.writeCharacters("\n");
        writer.writeDTD("<!DOCTYPE landscape SYSTEM \"landscape.dtd\">");
        writer.writeCharacters("\n");

        //Schreibe alle Landschaft Tags
        writer.writeStartElement("landscape-items");
        writer.writeAttribute("rows", landscape.getHeight() + "");
        writer.writeAttribute("cols", landscape.getWidth() + "");
        writer.writeCharacters("\n");

        for (int y = 0; y < landscape.getLandscape().length; y++) {

            for (int x = 0; x < landscape.getLandscape()[0].length; x++) {
                Field field = landscape.getField(y, x);
                if (landscape.getField(y, x) != Field.EMPTY) {
                    writer.writeCharacters("\t");
                    writer.writeStartElement("field");
                    writer.writeAttribute("type", field.toString());
                    writer.writeAttribute("y", "" + y);
                    writer.writeAttribute("x", "" + x);
                    writer.writeEndElement();
                    writer.writeCharacters("\n");
                }
            }

        }

        //Write Officer
        writer.writeCharacters("\t");
        writer.writeStartElement("officer");
        writer.writeAttribute("direction", landscape.getPoliceOfficer().getDirection().toString());
        writer.writeAttribute("numberOfFlags", landscape.getPoliceOfficer().getNumberOfFlags() + "");
        writer.writeAttribute("y", landscape.getPoliceOfficer().getyPos() + "");
        writer.writeAttribute("x", landscape.getPoliceOfficer().getxPos() + "");
        writer.writeEndElement(); // officer

        writer.writeCharacters("\n");
        writer.writeEndElement(); // landscape

        //Ende des Dokuments
        writer.writeEndDocument();
        writer.close();

    }

    public static void load(Controller controller, File xmlFile) {

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(new FileInputStream(xmlFile));

            loadXML(parser, controller);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadXML(XMLStreamReader parser, Controller controller) throws XMLStreamException {
        int rows = 0;
        int cols = 0;

        HashMap<Coordinates, Field> fields = new HashMap<>();

        int officerXPos = 0;
        int officerYPos = 0;
        Direction direction = Direction.NORTH;
        int numberOfFlags = 0;

        while (parser.hasNext()) {
            switch (parser.getEventType()) {
                case XMLStreamConstants.START_DOCUMENT:
                case XMLStreamConstants.END_DOCUMENT:
                    parser.close();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    String element = parser.getLocalName();
                    if ("landscape-items".equals(element)) {
                        rows = Integer.valueOf(parser.getAttributeValue(null, "rows"));
                        cols = Integer.valueOf(parser.getAttributeValue(null, "cols"));
                    } else if ("field".equals(element)) {
                        int y = Integer.valueOf(parser.getAttributeValue(null, "y"));
                        int x = Integer.valueOf(parser.getAttributeValue(null, "x"));
                        Field field = Field.valueOf(parser.getAttributeValue(null, "type"));
                        fields.put(new Coordinates(y, x), field);
                    } else if ("officer".equals(element)) {
                        officerYPos = Integer.valueOf(parser.getAttributeValue(null, "y"));
                        officerXPos = Integer.valueOf(parser.getAttributeValue(null, "x"));
                        direction = Direction.valueOf(parser.getAttributeValue(null, "direction"));
                        numberOfFlags = Integer.valueOf(parser.getAttributeValue(null, "numberOfFlags"));
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.END_ELEMENT:
                default:
                    break;
            }
            parser.next();
        }

        //resize
        Landscape landscape = controller.landscape;
        landscape.resize(cols, rows);

        //replace fields
        for (int y = 0; y < landscape.getLandscape().length; y++) {
            for (int x = 0; x < landscape.getLandscape()[0].length; x++) {
                landscape.getLandscape()[y][x] = Field.EMPTY;
            }
        }
        for (Coordinates coordinates : fields.keySet()) {
            for (int y = 0; y < landscape.getLandscape().length; y++) {
                for (int x = 0; x < landscape.getLandscape()[0].length; x++) {
                    if (y == coordinates.getY() && x == coordinates.getX()) {
                        landscape.getLandscape()[y][x] = fields.get(coordinates);
                    }
                }
            }
        }

        PoliceOfficer officer = landscape.getPoliceOfficer();
        officer.setNumberOfFlags(numberOfFlags);
        officer.setDirection(direction);
        officer.setxPos(officerXPos);
        officer.setyPos(officerYPos);

        controller.landscape = landscape;
        controller.landscape.reloadAfterDeserialization(officer);

        DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Erfolgreich", "Laden", "Landschaft wurde geladen!");

    }

}
