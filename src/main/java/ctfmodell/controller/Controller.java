package ctfmodell.controller;

import ctfmodell.model.Flag;
import ctfmodell.model.Landscape;
import ctfmodell.model.enums.FieldEnum;
import ctfmodell.model.exception.*;
import ctfmodell.util.Coordinates;
import ctfmodell.util.DialogProvider;
import ctfmodell.view.LandscapePanel;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

import java.util.Optional;


public class Controller {

    @FXML
    RadioMenuItem resizeMenu, avatarMenu, baseMenu, flagMenu, terroristUnarmedMenu, terroristArmedMenu, fieldMenu;

    @FXML
    ToggleButton sizeButton, avatarButton, baseButton, flagButton, terrorUnarmedButton, terrorArmedButton, deleteButton;

    @FXML
    ToggleGroup territoriumGroup, addingGroup;

    private Landscape landscape = new Landscape();
    private boolean moveAvatarEnabled = false;
    private boolean moveBaseEnabled = false;
    private FieldEnum itemToAdd = FieldEnum.OUT_OF_FIELD;
    private FieldEnum itemToDrag = FieldEnum.OUT_OF_FIELD;
    private boolean deleteEnabled = false;
    private boolean dragged;
    private LandscapePanel landscapePanel;

    private Coordinates originFieldYX;
    private EventHandler<MouseEvent> pressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            int originX = (int) Math.floor(mouseEvent.getX() - 1);
            int originY = (int) Math.floor(mouseEvent.getY() - 1);
            originFieldYX = landscape.getFieldByCoordinates(originY, originX);

            if (originFieldYX == null) {
                dragged = false;
                return;
            }

            if (moveAvatarEnabled) {
                FieldEnum field = landscape.getField(originFieldYX.getY(), originFieldYX.getX());
                dragged = field == FieldEnum.POLICE_OFFICER || field == FieldEnum.OFFICER_AND_BASE || field == FieldEnum.OFFICER_AND_FLAG;
            } else if (moveBaseEnabled) {
                FieldEnum field = landscape.getField(originFieldYX.getY(), originFieldYX.getX());
                dragged = field == FieldEnum.BASE || field == FieldEnum.OFFICER_AND_BASE;
            }
        }
    };
    private EventHandler<MouseEvent> dragEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (dragged) {
                int newX = (int) Math.floor(mouseEvent.getX());
                int newY = (int) Math.floor(mouseEvent.getY());
                Coordinates destinationFieldYX = landscape.getFieldByCoordinates(newY, newX);

                if (destinationFieldYX != null) {
                    FieldEnum field = landscape.getLandscape()[destinationFieldYX.getY()][destinationFieldYX.getX()];
                    //Ist origin Pair ungleich translate Pair?
                    if (!originFieldYX.getY().equals(destinationFieldYX.getY()) || !originFieldYX.getX().equals(destinationFieldYX.getX())) {
                        // Wenn ja, prüfe ob verschieben möglich ist
                        if (itemToDrag == FieldEnum.POLICE_OFFICER) {
                            if (field == FieldEnum.EMPTY || field == FieldEnum.BASE || field == FieldEnum.FLAG) {
                                landscape.clearOriginPolice(originFieldYX.getY(), originFieldYX.getX());
                                landscape.setDestinationPolice(destinationFieldYX.getY(), destinationFieldYX.getX());
                                originFieldYX = destinationFieldYX;
                            } else {
                                System.err.println("Police Officer kann nicht auf dieses Feld gesetzt werden!");
                            }
                        } else if (itemToDrag == FieldEnum.BASE) {
                            if (field == FieldEnum.EMPTY || field == FieldEnum.POLICE_OFFICER) {
                                landscape.clearOriginBase(originFieldYX.getY(), originFieldYX.getX());
                                landscape.setDestinationBase(destinationFieldYX.getY(), destinationFieldYX.getX());
                                originFieldYX = destinationFieldYX;
                            } else {
                                System.err.println("Die Base kann nicht auf dieses Feld gesetzt werden!");
                            }
                        }

                    }

                }

            }
        }
    };
    private EventHandler<MouseEvent> itemEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            int originX = (int) Math.floor(mouseEvent.getX() - 1);
            int originY = (int) Math.floor(mouseEvent.getY() - 1);
            if (!deleteEnabled && itemToAdd == FieldEnum.OUT_OF_FIELD) return;
            originFieldYX = landscape.getFieldByCoordinates(originY, originX);
            System.out.println("(Item Add) Clicked Coordinates: " + originY + "-" + originX);
            if (originFieldYX == null) return;

            if (deleteEnabled) {
                FieldEnum field = landscape.getField(originFieldYX.getY(), originFieldYX.getX());
                switch (field) {
                    case BASE:
                        System.err.println("Es gibts nichts zu löschen.");
                        break;
                    case OFFICER_AND_BASE:
                    case POLICE_OFFICER:
                        System.err.println("Der Akteur kann nicht gelöscht werden.");
                        break;
                    case FLAG:
                    case UNARMED_TERRORIST:
                    case ARMED_TERRORIST:
                        landscape.setField(originFieldYX.getY(), originFieldYX.getX(), FieldEnum.EMPTY);
                        break;
                    case OFFICER_AND_FLAG:
                        landscape.setField(originFieldYX.getY(), originFieldYX.getX(), FieldEnum.FLAG);
                        break;
                }
            } else if (itemToAdd != FieldEnum.OUT_OF_FIELD) {
                switch (itemToAdd) {
                    case FLAG:
                        Flag flagToAdd = new Flag(originFieldYX.getX(), originFieldYX.getY());
                        try {
                            landscape.addFlag(flagToAdd);
                        } catch (LandscapeException e) {
                            System.err.println(e.getMessage());
                        }
                        break;
                    case UNARMED_TERRORIST:
                        try {
                            landscape.addUnarmedTerrorist(originFieldYX.getY(), originFieldYX.getX());
                        } catch (LandscapeException e) {
                            System.err.println(e.getMessage());
                        }
                        break;
                    case ARMED_TERRORIST:
                        try {
                            landscape.addArmedTerrorist(originFieldYX.getY(), originFieldYX.getX());
                        } catch (LandscapeException e) {
                            System.err.println(e.getMessage());
                        }
                        break;
                }
            }

        }
    };

    @FXML
    public void initialize(Landscape landscape, LandscapePanel landscapePanel) {
        this.landscape = landscape;
        this.landscapePanel = landscapePanel;
    }

    public void initializeEventHandler() {
        landscapePanel.setOnMousePressed(pressedEventHandler);
        landscapePanel.setOnMouseDragged(dragEventHandler);
        landscapePanel.setOnMouseClicked(itemEventHandler);
    }

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
            this.moveAvatarEnabled = true;
            this.itemToDrag = FieldEnum.POLICE_OFFICER;
        } else if (str.contains("baseMenu")) {
            baseButton.setSelected(true);
            this.moveBaseEnabled = true;
            this.itemToDrag = FieldEnum.BASE;
        } else if (str.contains("flagMenu")) {
            flagButton.setSelected(true);
            this.itemToAdd = FieldEnum.FLAG;
        } else if (str.contains("terroristUnarmedMenu")) {
            terrorUnarmedButton.setSelected(true);
            this.itemToAdd = FieldEnum.UNARMED_TERRORIST;
        } else if (str.contains("terroristArmedMenu")) {
            terrorArmedButton.setSelected(true);
            this.itemToAdd = FieldEnum.ARMED_TERRORIST;
        } else if (str.contains("fieldMenu")) {
            deleteButton.setSelected(true);
            this.deleteEnabled = true;
        }
    }

    private void disableStates(ToggleGroup addingGroup) {
        this.moveAvatarEnabled = false;
        this.moveBaseEnabled = false;
        this.deleteEnabled = false;
        this.dragged = false;
        this.itemToAdd = FieldEnum.OUT_OF_FIELD;
        this.itemToDrag = FieldEnum.OUT_OF_FIELD;
        System.err.println(addingGroup.getSelectedToggle());
    }

    private void resize() {
        resizeMenu.setSelected(true);

        Dialog<Pair<String, String>> dialog = DialogProvider.getSizeDialog();
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(hoeheBreite -> {
            try {
                int height = Integer.parseInt(hoeheBreite.getKey().trim());
                int width = Integer.parseInt(hoeheBreite.getValue().trim());
                this.landscape.resize(width, height);
            } catch (NumberFormatException ex) {
                System.err.println("Es wurden nicht valide Zahlen eingegeben!");
            } catch (LandscapeException ex) {
                System.err.println("Es ist ein Fehler beim resizen aufgetreten.");
            }
        });

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
            this.moveAvatarEnabled = true;
            this.itemToDrag = FieldEnum.POLICE_OFFICER;
        } else if (str.contains("baseButton")) {
            baseButton.setSelected(true);
            this.moveBaseEnabled = true;
            this.itemToDrag = FieldEnum.BASE;
        } else if (str.contains("flagButton")) {
            flagMenu.setSelected(true);
            this.itemToAdd = FieldEnum.FLAG;
        } else if (str.contains("terrorUnarmedButton")) {
            terroristUnarmedMenu.setSelected(true);
            this.itemToAdd = FieldEnum.UNARMED_TERRORIST;
        } else if (str.contains("terrorArmedButton")) {
            terroristArmedMenu.setSelected(true);
            this.itemToAdd = FieldEnum.ARMED_TERRORIST;
        } else if (str.contains("deleteButton")) {
            fieldMenu.setSelected(true);
            this.deleteEnabled = true;
        }
    }

    @FXML
    private void hasFlags() {
        if (this.landscape == null) return;
        if (this.landscape.getPoliceOfficer() == null) return;
        int count = this.landscape.getPoliceOfficer().getNumberOfFlags();
        DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Anzahl der Flaggen", "Anzahl", "Der Officer hat " + count + " Flaggen.");
    }

    @FXML
    private void turnLeft() {
        this.landscape.getPoliceOfficer().turnLeft();
    }

    @FXML
    private void forward() {
        try {
            this.landscape.getPoliceOfficer().forward();
        } catch (MoveException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void pick() {
        try {
            this.landscape.getPoliceOfficer().pick();
        } catch (FlagException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void drop() {
        try {
            this.landscape.getPoliceOfficer().drop();
            if (this.landscape.getPoliceOfficer().hasWon()) {
                DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Mission", "Mission", "Mission erfüllt!");
            }
        } catch (FlagException | BaseException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void attack() {
        try {
            this.landscape.getPoliceOfficer().attack();
        } catch (PoliceException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
