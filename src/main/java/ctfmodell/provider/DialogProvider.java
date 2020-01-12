package ctfmodell.provider;

import ctfmodell.model.Example;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.List;

@SuppressWarnings("Duplicates")
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

    public static Dialog<String> getSingleTextBoxDialog(String title, String header, String button, String label) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        ButtonType resizeButtonType = new ButtonType(button, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resizeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField className = new TextField();
        className.setPromptText(label);

        grid.add(new Label(label), 0, 0);
        grid.add(className, 1, 0);

        Node okButton = dialog.getDialogPane().lookupButton(resizeButtonType);
        okButton.setDisable(true);
        className.textProperty().addListener((observable, oldValue, newValue) -> okButton.setDisable(newValue.trim().isEmpty()));
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(className::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resizeButtonType) {
                return className.getText();
            }
            return null;
        });

        return dialog;
    }

    public static ChoiceDialog<Example> getExampleChoiceBox(List<Example> examples) {
        ChoiceDialog<Example> dialog = new ChoiceDialog<>(examples.get(0), examples);

        dialog.setTitle("Verfügbaren Beispiele");
        dialog.setHeaderText("Wähle ein Beispiel aus:");
        dialog.setContentText("Beispiel:");

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
