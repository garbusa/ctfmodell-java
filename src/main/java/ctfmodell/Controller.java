package ctfmodell;

import ctfmodell.gui.LandscapePanel;
import ctfmodell.model.enums.FieldEnum;
import ctfmodell.model.exception.*;
import ctfmodell.util.PixelRectangle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.text.NumberFormat;
import java.util.Optional;


public class Controller {

    @FXML
    RadioMenuItem resizeMenu, avatarMenu, flagMenu, terroristUnarmedMenu, terroristArmedMenu, fieldMenu;

    @FXML
    ToggleButton sizeButton, avatarButton, flagButton, terrorUnarmedButton, terrorArmedButton, deleteButton;

    @FXML
    LandscapePanel landscapePanel;

    @FXML
    ToggleGroup territoriumGroup, addingGroup;

    @FXML
    private void territoriumGroup() {
        disableStates(territoriumGroup);
        RadioMenuItem item = (RadioMenuItem) territoriumGroup.getSelectedToggle();

        if (item == null) return;
        String str = item.getId();

        if (str.contains("resizeMenu")) {
            sizeButton.setSelected(true);
            this.resize();
        } else if (str.contains("avatarMenu")) {
            avatarButton.setSelected(true);
            landscapePanel.setMoveEnabled(true);
        } else if (str.contains("flagMenu")) {
            flagButton.setSelected(true);
            landscapePanel.setItemToAdd(FieldEnum.FLAG);
        } else if (str.contains("terroristUnarmedMenu")) {
            terrorUnarmedButton.setSelected(true);
            landscapePanel.setItemToAdd(FieldEnum.UNARMED_TERRORIST);
        } else if (str.contains("terroristArmedMenu")) {
            terrorArmedButton.setSelected(true);
            landscapePanel.setItemToAdd(FieldEnum.ARMED_TERRORIST);
        } else if (str.contains("fieldMenu")) {
            deleteButton.setSelected(true);
            landscapePanel.setDeleteEnabled(true);
        }
    }

    @FXML
    private void addingGroup() {
        disableStates(addingGroup);
        ToggleButton button = (ToggleButton) addingGroup.getSelectedToggle();

        if (button == null) return;
        String str = button.getId();

        if (str.contains("sizeButton")) {
            resizeMenu.setSelected(true);
            this.resize();
        } else if (str.contains("avatarButton")) {
            avatarMenu.setSelected(true);
            landscapePanel.setMoveEnabled(true);
        } else if (str.contains("flagButton")) {
            flagMenu.setSelected(true);
            landscapePanel.setItemToAdd(FieldEnum.FLAG);
        } else if (str.contains("terrorUnarmedButton")) {
            terroristUnarmedMenu.setSelected(true);
            landscapePanel.setItemToAdd(FieldEnum.UNARMED_TERRORIST);
        } else if (str.contains("terrorArmedButton")) {
            terroristArmedMenu.setSelected(true);
            landscapePanel.setItemToAdd(FieldEnum.ARMED_TERRORIST);
        } else if (str.contains("deleteButton")) {
            fieldMenu.setSelected(true);
            landscapePanel.setDeleteEnabled(true);
        }
    }

    private void disableStates(ToggleGroup addingGroup) {
        landscapePanel.setMoveEnabled(false);
        landscapePanel.setDeleteEnabled(false);
        landscapePanel.setDragged(false);
        landscapePanel.setItemToAdd(FieldEnum.OUT_OF_FIELD);
        System.err.println(addingGroup.getSelectedToggle());
    }

    @FXML
    private void hasFlags() {
        if (landscapePanel.getLandscape() == null) return;
        if (landscapePanel.getLandscape().getPoliceOfficer() == null) return;
        int count = landscapePanel.getLandscape().getPoliceOfficer().getNumberOfFlags();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Anzahl der Flaggen");
        alert.setHeaderText("Anzahl");
        alert.setContentText("Der Officer hat " + count + " Flaggen.");
        alert.showAndWait();
    }

    @FXML
    private void turnLeft() {
        landscapePanel.getLandscape().getPoliceOfficer().turnLeft();
        landscapePanel.draw();
    }

    @FXML
    private void forward() {
        try {
            landscapePanel.getLandscape().getPoliceOfficer().forward();
        } catch (MoveException ex) {
            System.err.println(ex.getMessage());
        }
        landscapePanel.draw();
    }

    @FXML
    private void pick() {
        try {
            landscapePanel.getLandscape().getPoliceOfficer().pick();
        } catch (FlagException ex) {
            System.err.println(ex.getMessage());
        }
        landscapePanel.draw();
    }

    @FXML
    private void drop() {
        try {
            landscapePanel.getLandscape().getPoliceOfficer().drop();
            if (landscapePanel.getLandscape().getPoliceOfficer().hasWon()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Mission");
                alert.setHeaderText("Mission");
                alert.setContentText("Du hast deine Mission erfüllt!");
                alert.showAndWait();
            }
        } catch (FlagException | BaseException ex) {
            System.err.println(ex.getMessage());
        }
        landscapePanel.draw();
    }

    @FXML
    private void attack() {
        try {
            landscapePanel.getLandscape().getPoliceOfficer().attack();
        } catch (PoliceException ex) {
            System.err.println(ex.getMessage());
        }
        landscapePanel.draw();
    }


    private void resize() {
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

        Platform.runLater(heightField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resizeButtonType) {
                return new Pair<>(heightField.getText(), widthField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(hoeheBreite -> {
            System.out.println("Höhe=" + hoeheBreite.getKey() + ", Breite=" + hoeheBreite.getValue());

            try {
                int height = Integer.parseInt(hoeheBreite.getKey().trim());
                int width = Integer.parseInt(hoeheBreite.getValue().trim());
                landscapePanel.getLandscape().resize(width, height);
                landscapePanel.setLandscapeCoordinates(new PixelRectangle[height][width]);
                landscapePanel.updateCanvasSizeAndDraw();

                System.out.println("Größe wurde geändert");
            } catch(NumberFormatException ex){
                System.err.println("Es wurden nicht valide Zahlen eingegeben!");
            }
            catch (LandscapeException ex) {
                System.err.println("Es ist ein Fehler beim resizen aufgetreten.");
            }
        });

    }


}
