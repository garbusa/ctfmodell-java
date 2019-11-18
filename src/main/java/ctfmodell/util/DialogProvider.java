package ctfmodell.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class DialogProvider {

    public static Dialog<Pair<String, String>> getSizeDialog() {
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

        heightField.textProperty().addListener((observable, oldValue, newValue) -> okButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(heightField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resizeButtonType) {
                return new Pair<>(heightField.getText(), widthField.getText());
            }
            return null;
        });

        return dialog;
    }

    public static void alert(Alert.AlertType alertType, String title, String header, String text) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }

}
