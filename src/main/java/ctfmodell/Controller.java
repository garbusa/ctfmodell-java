package ctfmodell;

import ctfmodell.gui.LandscapePanel;
import ctfmodell.model.exception.LandscapeException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;


public class Controller {

    @FXML
    RadioMenuItem resizeMenu, avatarMenu, flagMenu, terroristMenu, fieldMenu;


    @FXML
    LandscapePanel landscapePanel;

    @FXML
    private void resize(ActionEvent e) {
        System.out.println("resizing...");
        resizeMenu.setSelected(true);

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Neue Größe festlegen");
        dialog.setHeaderText("Gebe die Höhe und Breite ein!");

        ButtonType resizeButtonType = new ButtonType("Größe ändern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resizeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField heightField = new TextField();
        heightField.setPromptText("Höhe");
        TextField widthField = new TextField();
        widthField.setPromptText("Breite");

        grid.add(new Label("Höhe:"), 0, 0);
        grid.add(heightField, 1, 0);
        grid.add(new Label("Breite:"), 0, 1);
        grid.add(widthField, 1, 1);

        Node okButton = dialog.getDialogPane().lookupButton(resizeButtonType);
        okButton.setDisable(true);

        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> heightField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resizeButtonType) {
                return new Pair<>(heightField.getText(), widthField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(hoeheBreite -> {
            System.out.println("Höhe=" + hoeheBreite.getKey() + ", Breite=" + hoeheBreite.getValue());
            int height = Integer.parseInt(hoeheBreite.getKey().trim());
            int width = Integer.parseInt(hoeheBreite.getValue().trim());
            try {
                landscapePanel.getLandscape().resize(width, height);
                landscapePanel.draw();
                System.out.println("Größe wurde geändert");
            } catch (LandscapeException ex) {
                System.err.println("Es ist ein Fehler beim resizen aufgetreten.");
            }
        });



    }

    @FXML
    private void moveAvatar(ActionEvent e) {
        avatarMenu.setSelected(true);
    }

}
