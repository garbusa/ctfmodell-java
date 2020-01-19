package ctfmodell.tutor;

import ctfmodell.Main;
import ctfmodell.controller.Controller;
import ctfmodell.model.Landscape;
import ctfmodell.model.StudentExample;
import ctfmodell.serialization.XMLSerialization;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.UUID;


public class TutorController {

    @FXML
    MenuItem sendOrLoadRequest;

    @FXML
    MenuItem sendOrLoadAnswer;

    private String studentId = UUID.randomUUID().toString();
    private String studendIdToAnswer = null;

    private Controller mainController;
    private String role = null;

    private boolean connectionFailed = false;

    @FXML
    public void initialize() {
        if (this.role != null) {
            setRoleMenu();
        }
    }

    private void setRoleMenu() {
        switch (this.role) {
            case "tutor":
                sendOrLoadRequest.setText("Anfrage laden");
                sendOrLoadAnswer.setText("Anfrage beantworten");
                sendOrLoadRequest.setOnAction((event) -> this.loadRequest());
                sendOrLoadAnswer.setOnAction((event) -> this.sendAnswer());

                break;
            case "student":
                sendOrLoadRequest.setText("Tutoranfrage senden");
                sendOrLoadAnswer.setText("Tutoranfrage laden");
                sendOrLoadRequest.setOnAction((event) -> this.sendRequest());
                sendOrLoadAnswer.setOnAction((event) -> this.loadAnswer());

                break;
            default:
                throw new IllegalStateException("unexpected role");
        }
    }

    private void loadRequest() {
        StudentExample example;
        try {
            example = Tutor.tutorialSystem.loadNextRequest();

            if (example == null) {
                System.out.println("Es gibt momentan keine Anfragen von Studenten!");
            } else {
                this.studendIdToAnswer = example.getStudentId();
                Tutor.loadStudentExample(mainController, example);
                System.out.println("Anfrage von Student wurde geladen!");
            }
        } catch (RemoteException e) {
            System.out.println("Es es beim laden einer Studentenanfrage ein Fehler aufgetreten!");
            e.printStackTrace();
        }
    }

    private void sendAnswer() {
        if (studendIdToAnswer == null) {
            System.out.println("Du hast noch keine Anfrage geladen!");
            return;
        }

        try {
            StudentExample example = deserializeExample(studendIdToAnswer);
            Tutor.saveStudentExample(example);
            studendIdToAnswer = null;
            System.out.println("Antwort wurde vom Tutor gespeichert");
        } catch (XMLStreamException e) {
            System.out.println("Speichern der Antwort ist fehlgeschlagen!");
            e.printStackTrace();
        }
    }

    private void sendRequest() {
        try {
            if (connectionFailed) {
                Main.establishRMIConnection(this.role);
            }

            if (Tutor.tutorialSystem == null) {
                System.err.println("Es besteht keine Verbindung zu einem Tutor!");
                return;
            }

            StudentExample example = deserializeExample(studentId);
            boolean sendRequestSuccess = Tutor.tutorialSystem.sendRequest(example);

            if (sendRequestSuccess) {
                System.out.println("Anfrage an den Tutor wurde erfolgreich versendet");
            } else {
                System.out.println("Anfrage an den Tutor konnte nicht versendet werden");
            }


        } catch (RemoteException | XMLStreamException e) {
            connectionFailed = true;
            System.err.println("Anfrage an den Tutor konnte nicht versendet werden");
            //e.printStackTrace();
        }
    }

    private void loadAnswer() {
        if (connectionFailed) {
            Main.establishRMIConnection(this.role);
        }

        if (Tutor.tutorialSystem == null) {
            System.err.println("Es besteht keine Verbindung zu einem Tutor!");
            return;
        }

        StudentExample example;
        try {
            example = Tutor.tutorialSystem.checkResponse(studentId);
            if (example == null) {
                System.out.println("Es gibt momentan keine Antworten vom Tutor!");
            } else {
                Tutor.loadStudentExample(mainController, example);
                System.out.println("Antwort vom Studenten wurde geladen!");
            }
        } catch (RemoteException e) {
            connectionFailed = true;
            System.err.println("Es es beim laden der Tutor-Antwort ein Fehler aufgetreten!");
            //e.printStackTrace();
        }
    }

    private StudentExample deserializeExample(String studendId) throws XMLStreamException {
        Landscape landscape = mainController.landscape;
        String code = mainController.getCodeEditor().getText();
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(stringWriter);
        XMLSerialization.writeXML(xmlWriter, landscape);
        String landscapeXML = stringWriter.toString();

        return new StudentExample(studendId, code, landscapeXML);
    }

    public void setParentController(Controller controller) {
        this.mainController = controller;
    }

    public void setRole(String role) {
        this.role = role;
        setRoleMenu();
    }

}
