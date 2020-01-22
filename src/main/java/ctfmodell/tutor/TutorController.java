package ctfmodell.tutor;

import ctfmodell.Main;
import ctfmodell.controller.Controller;
import ctfmodell.model.Landscape;
import ctfmodell.model.StudentExample;
import ctfmodell.provider.DialogProvider;
import ctfmodell.serialization.XMLSerialization;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.ResourceBundle;
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
    private ResourceBundle resourceBundle;

    @FXML
    public void initialize() {
        if (this.role != null) {
            setRoleMenu("de");
        }

    }

    private void setRoleMenu(String language) {
        resourceBundle = ResourceBundle.getBundle("bundles.language", new Locale(language));

        switch (this.role) {
            case "tutor":
                sendOrLoadRequest.setText(resourceBundle.getString("menuItemStudentAnfrageLaden"));
                sendOrLoadAnswer.setText(resourceBundle.getString("menuItemStudentAntwortSenden"));
                sendOrLoadRequest.setOnAction((event) -> this.loadRequest());
                sendOrLoadAnswer.setOnAction((event) -> this.sendAnswer());

                sendOrLoadRequest.setDisable(false);
                sendOrLoadAnswer.setDisable(true);
                break;
            case "student":
                sendOrLoadRequest.setText(resourceBundle.getString("menuItemTutorAnfrageSenden"));
                sendOrLoadAnswer.setText(resourceBundle.getString("menuItemTutorAntwortLaden"));
                sendOrLoadRequest.setOnAction((event) -> this.sendRequest());
                sendOrLoadAnswer.setOnAction((event) -> this.loadAnswer());

                sendOrLoadRequest.setDisable(false);
                sendOrLoadAnswer.setDisable(true);
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
                DialogProvider.alert(
                        Alert.AlertType.ERROR, "Keine Anfragen", "Keine Anfragen",
                        "Es gibt momentan keine Anfragen von Studenten"
                );
            } else {
                this.studendIdToAnswer = example.getStudentId();
                Tutor.loadStudentExample(mainController, example);
                mainController.compile();
                DialogProvider.alert(
                        Alert.AlertType.CONFIRMATION, "Anfragen", "Geladen",
                        "Anfrage vom Studenten wurde geladen"
                );
                switchControlsEnable();
            }
        } catch (RemoteException e) {
            DialogProvider.alert(
                    Alert.AlertType.ERROR, "Fehler", "Fehler",
                    "Beim Laden einer Studentenanfrage ist ein Fehler aufgetreten"
            );
        }
    }

    private void sendAnswer() {
        if (studendIdToAnswer == null) {
            DialogProvider.alert(Alert.AlertType.INFORMATION, "Anfragen", "Wichtig",
                    "Du musst vorher eine Anfrage eines Studenten laden!");
            return;
        }

        try {
            StudentExample example = deserializeExample(studendIdToAnswer);
            Tutor.saveStudentExample(example);
            studendIdToAnswer = null;
            DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Gespeichert", "Gespeichert",
                    "Deine Antwort wurde für den Studenten hinterlegt!");
            switchControlsEnable();
        } catch (XMLStreamException e) {
            DialogProvider.alert(Alert.AlertType.ERROR, "Fehler", "Fehler",
                    "Speichern der Antwort ist fehlgeschlagen!");
            e.printStackTrace();
        }
    }

    private void sendRequest() {
        try {
            if (connectionFailed()) return;

            StudentExample example = deserializeExample(studentId);
            boolean sendRequestSuccess = Tutor.tutorialSystem.sendRequest(example);

            if (sendRequestSuccess) {
                DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Erfolgreich", "Versendet",
                        "Anfrage an den Tutor wurde erfolgreich versendet");
                switchControlsEnable();
            } else {
                connectionFailed = true;
                DialogProvider.alert(Alert.AlertType.ERROR, "Fehler", "Fehler",
                        "Deine Anfrage konnte nicht versendet werden.");
            }


        } catch (RemoteException | XMLStreamException e) {
            connectionFailed = true;
            DialogProvider.alert(Alert.AlertType.ERROR, "Fehler", "Fehler",
                    "Deine Anfrage konnte nicht versendet werden.");
            //e.printStackTrace();
        }
    }

    private boolean connectionFailed() {
        if (connectionFailed) {
            Main.establishRMIConnection(this.role);
        }

        if (Tutor.tutorialSystem == null) {
            DialogProvider.alert(Alert.AlertType.ERROR, "Fehler", "Keine Verbindung", "" +
                    "Es konnte keine Verbindung zum Tutor aufgebaut werden!");
            return true;
        }
        return false;
    }

    private void loadAnswer() {
        if (connectionFailed()) return;

        StudentExample example;
        try {
            example = Tutor.tutorialSystem.checkResponse(studentId);
            if (example == null) {
                DialogProvider.alert(Alert.AlertType.INFORMATION, "Information", "Keine Antwort",
                        "Es gibt momentan keine Antworten vom Tutor!");
            } else {
                Tutor.loadStudentExample(mainController, example);
                mainController.compile();
                DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Erfolgreich", "Geladen",
                        "Antwort vom Tutor wurde geladen!");
                switchControlsEnable();
            }
        } catch (RemoteException e) {
            connectionFailed = true;
            DialogProvider.alert(Alert.AlertType.ERROR, "Fehler", "Fehler",
                    "Es es beim laden der Tutor-Antwort ein Fehler aufgetreten!");
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

    public void setRole(String role, String language) {
        this.role = role;
        setRoleMenu(language);
    }

    private void switchControlsEnable() {
        if (this.sendOrLoadAnswer.isDisable()) {
            this.sendOrLoadAnswer.setDisable(false);
        } else {
            this.sendOrLoadAnswer.setDisable(true);
        }

        if (this.sendOrLoadRequest.isDisable()) {
            this.sendOrLoadRequest.setDisable(false);
        } else {
            this.sendOrLoadRequest.setDisable(true);
        }
    }

}
