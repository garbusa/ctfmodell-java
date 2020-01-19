package ctfmodell.tutor;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class TutorController {

    @FXML
    MenuItem sendOrLoadRequest;

    @FXML
    MenuItem sendOrLoadAnswer;


    @FXML
    public void initialize() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = new FileInputStream("simulator.properties");
            properties.load(inputStream);

            String role = properties.getProperty("role");

            switch (role) {
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


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest() {
        System.out.println("Student schickt Anfrage an Tutor");
    }

    private void loadRequest() {
        System.out.println("Tutor lädt Anfrage vom Student");
    }

    private void sendAnswer() {
        System.out.println("Tutor verschickt Antwort an Student");
    }

    private void loadAnswer() {
        System.out.println("Student lädt Antwort vom Tutor");
    }

}
