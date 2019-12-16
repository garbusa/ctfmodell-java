package ctfmodell.controller;

import ctfmodell.Main;
import ctfmodell.model.Flag;
import ctfmodell.model.Landscape;
import ctfmodell.model.PoliceOfficer;
import ctfmodell.model.enums.Field;
import ctfmodell.model.exception.*;
import ctfmodell.provider.DialogProvider;
import ctfmodell.serialization.LandscapeSerialization;
import ctfmodell.serialization.XMLSerialization;
import ctfmodell.simulation.SimulationRunner;
import ctfmodell.util.Coordinates;
import ctfmodell.util.Helper;
import ctfmodell.view.LandscapePanel;
import ctfmodell.view.OfficerContextMenu;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

import static ctfmodell.Main.*;


public class Controller {

    public Landscape landscape;
    @FXML
    BorderPane pane;
    @FXML
    MenuItem newItem;
    @FXML
    RadioMenuItem resizeMenu, avatarMenu, baseMenu, flagMenu, terroristUnarmedMenu, terroristArmedMenu, fieldMenu;
    @FXML
    ToggleButton sizeButton, avatarButton, baseButton, flagButton, terrorUnarmedButton, terrorArmedButton, deleteButton;
    @FXML
    ToggleGroup territoriumGroup, addingGroup;
    @FXML
    TextArea codeEditor;
    @FXML
    Slider slider;
    private boolean moveAvatarEnabled = false;
    private boolean moveBaseEnabled = false;
    private Field itemToAdd = Field.OUT_OF_FIELD;
    private Field itemToDrag = Field.OUT_OF_FIELD;
    private boolean dragged;
    private LandscapePanel landscapePanel;
    private String editorClass;
    private Stage stage;
    private OfficerContextMenu contextMenu;
    private SimulationRunner runner;

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
                Field field = landscape.getField(originFieldYX.getY(), originFieldYX.getX());
                dragged = field == Field.POLICE_OFFICER || field == Field.OFFICER_AND_BASE || field == Field.OFFICER_AND_FLAG;
            } else if (moveBaseEnabled) {
                Field field = landscape.getField(originFieldYX.getY(), originFieldYX.getX());
                dragged = field == Field.BASE || field == Field.OFFICER_AND_BASE;
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
                    Field field = landscape.getLandscape()[destinationFieldYX.getY()][destinationFieldYX.getX()];
                    //Ist origin Pair ungleich translate Pair?
                    if (!originFieldYX.getY().equals(destinationFieldYX.getY()) || !originFieldYX.getX().equals(destinationFieldYX.getX())) {
                        // Wenn ja, prüfe ob verschieben möglich ist
                        if (itemToDrag == Field.POLICE_OFFICER) {
                            if (field == Field.EMPTY || field == Field.BASE || field == Field.FLAG) {
                                landscape.clearOriginPolice(originFieldYX.getY(), originFieldYX.getX());
                                landscape.setDestinationPolice(destinationFieldYX.getY(), destinationFieldYX.getX());
                                originFieldYX = destinationFieldYX;
                            } else {
                                System.err.println("Police Officer kann nicht auf dieses Feld gesetzt werden!");
                            }
                        } else if (itemToDrag == Field.BASE) {
                            if (field == Field.EMPTY || field == Field.POLICE_OFFICER) {
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
            if (!landscape.isDeleteEnabled() && itemToAdd == Field.OUT_OF_FIELD) return;
            originFieldYX = landscape.getFieldByCoordinates(originY, originX);
            System.out.println("(Item Add) Clicked Coordinates: " + originY + "-" + originX);
            if (originFieldYX == null) return;

            if (landscape.isDeleteEnabled()) {
                Field field = landscape.getField(originFieldYX.getY(), originFieldYX.getX());
                switch (field) {
                    case BASE:
                        System.err.println("Es gibts nichts zu löschen.");
                        break;
                    case OFFICER_AND_BASE:
                    case POLICE_OFFICER:
                        System.err.println("Der Akteur kann nicht gelöscht werden.");
                        break;
                    case FLAG:
                        landscape.deleteFlag(originFieldYX.getY(), originFieldYX.getX());
                        landscape.setField(originFieldYX.getY(), originFieldYX.getX(), Field.EMPTY, false);
                        break;
                    case UNARMED_TERRORIST:
                    case ARMED_TERRORIST:
                        landscape.setField(originFieldYX.getY(), originFieldYX.getX(), Field.EMPTY, false);
                        break;
                    case OFFICER_AND_FLAG:
                        landscape.setField(originFieldYX.getY(), originFieldYX.getX(), Field.POLICE_OFFICER, false);
                        break;
                }
            } else if (itemToAdd != Field.OUT_OF_FIELD) {
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
    private EventHandler<ContextMenuEvent> contextMenuHandler = new EventHandler<ContextMenuEvent>() {
        @Override
        public void handle(ContextMenuEvent event) {
            contextMenu.show(stage, event.getSceneX(), event.getSceneY());
        }
    };

    @FXML
    public void initialize(Landscape landscape, LandscapePanel landscapePanel, String code) {
        this.landscape = landscape;
        this.landscapePanel = landscapePanel;
        runner = new SimulationRunner(0, this.landscape);
        slider.valueProperty().addListener(
                (observable, oldValue, newValue) -> runner.setSpeed(newValue.doubleValue())
        );
        codeEditor.setText(code);
        this.compile(false);
        this.initializeContextMenu(landscape.getPoliceOfficer().getClass().getName());
    }

    /**
     * Inspiriert aus CompileWithDiagnostics.java
     */
    private void compile(boolean withAlert) {
        this.saveCode();

        Path codeFile = Paths.get(Main.PROGAM_FOLDER, editorClass + ".java");
        Path programsFolder = Paths.get(Main.PROGAM_FOLDER);

        ClassLoader cl = getClassLoader(programsFolder);

        if (cl == null) {
            System.err.println("Classloader konnte nicht geladen werden!");
            return;
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(Collections.singleton(codeFile.toFile()));
        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, units);

        System.out.println("Compilation success: " + task.call());

        if (!diagnostics.getDiagnostics().isEmpty()) {
            Diagnostic<?> diagnostic = diagnostics.getDiagnostics().get(0);
            String compileMessage = "Quelle: " + diagnostic.getSource() +
                    "\nNachricht: " +
                    diagnostic.getMessage(null) +
                    "\nZeile: " +
                    diagnostic.getLineNumber();
            if (withAlert) {
                DialogProvider.alert(Alert.AlertType.ERROR, "Kompilierfehler", "Kompilierfehler",
                        compileMessage);
            }
            PoliceOfficer policeOfficer = new PoliceOfficer();
            this.landscape.updatePoliceOfficer(policeOfficer);
            this.initializeContextMenu(policeOfficer.getClass().getName());
            this.deleteAndUpdateObserver();
        } else {
            try {
                if (withAlert) {
                    DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Kompiliert", "Kompiliert",
                            "Ihr Programmcode wurde erfolgreich kompiliert!");
                }
                PoliceOfficer compiledOfficer = (PoliceOfficer) cl.loadClass(editorClass).newInstance();
                System.out.println(compiledOfficer);
                this.landscape.updatePoliceOfficer(compiledOfficer);
                this.initializeContextMenu(compiledOfficer.getClass().getName());
                this.deleteAndUpdateObserver();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    private void initializeContextMenu(String officerType) {
        contextMenu = new OfficerContextMenu(officerType, this.landscape);
        landscapePanel.setOnContextMenuRequested(contextMenuHandler);
    }

    @FXML
    public void saveCode() {
        String prefix = PREFIX_1 + editorClass + PREFIX_2;
        Path codeFile = Paths.get(Main.PROGAM_FOLDER, editorClass + ".java");

        StringBuilder builder = new StringBuilder(prefix);
        builder.append(codeEditor.getText())
                .append(POSTFIX);

        try {
            Files.write(codeFile, builder.toString().getBytes());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private ClassLoader getClassLoader(Path programsFolder) {
        ClassLoader cl = null;
        try {
            URL[] urls = new URL[]{programsFolder.toFile().toURI().toURL()};
            cl = new URLClassLoader(urls);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        return cl;
    }

    private void deleteAndUpdateObserver() {
        if (this.landscape.getPoliceOfficer() != null) {
            this.landscape.getPoliceOfficer().deleteObservers();
            this.landscape.getPoliceOfficer().addObserver(landscapePanel);
            this.landscape.getPoliceOfficer().addObserver(this.runner);
        }

        this.landscape.deleteObservers();
        this.landscape.addObserver(landscapePanel);
        this.landscape.addObserver(this.runner);
        this.runner.setLandscape(this.landscape);
    }

    public void initializeEventHandler() {
        landscapePanel.setOnMousePressed(pressedEventHandler);
        landscapePanel.setOnMouseDragged(dragEventHandler);
        landscapePanel.setOnMouseClicked(itemEventHandler);
    }

    public void setEditorClass(String editorClass) {
        this.editorClass = editorClass;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void compile() {
        this.compile(true);
    }

    @FXML
    public void play() {
        if (!runner.running && !runner.paused) {
            new Thread(runner).start();
        } else {
            runner.resume();
        }

    }

    @FXML
    public void pause() {
        runner.pause();
    }

    @FXML
    public void stop() {
        runner.stop();
    }

    @FXML
    public void loadLandscape(ActionEvent event) {
        LandscapeSerialization serialization = new LandscapeSerialization();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Paths.get(LANDSCAPE_FOLDER).toFile());
        File landscapeFile = fileChooser.showOpenDialog(pane.getScene().getWindow());

        if (landscapeFile == null) {
            System.err.println("Keine Datei ausgewählt!");
            return;
        }

        String file = landscapeFile.getName();
        String fileName = (file.contains(".landscape")) ? file.split("\\.")[0] : null;

        if (fileName != null) {
            serialization.deserialize(this, landscapeFile, landscapePanel);
        } else {
            System.err.println("Die ausgewählte Datei ist keine gültige Landscape-Datei!");
        }


    }

    @FXML
    public void saveLandscape(ActionEvent event) {
        LandscapeSerialization serialization = new LandscapeSerialization();

        Dialog<String> dialog = DialogProvider.getSingleTextBoxDialog("Landschaft abspeichern", "Gebe den Landschaftsnamen ein", "speichern", "Dateiname");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            Path directory = Paths.get(LANDSCAPE_FOLDER, name + ".landscape");
            if (!Files.exists(directory)) {
                serialization.serialize(this.landscape, directory);
            } else {
                System.err.println(name + ".landscape existiert schon. Wähle einen anderen Namen aus!");
            }
        });

    }

    @FXML
    public void saveXML(ActionEvent actionEvent) {
        XMLSerialization serializator = new XMLSerialization();

        Dialog<String> dialog = DialogProvider.getSingleTextBoxDialog("Landschaft abspeichern (XML)", "Gebe den Landschaftsnamen ein", "speichern", "Dateiname");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            Path directory = Paths.get(XML_FOLDER, name + ".txml");
            if (!Files.exists(directory)) {
                serializator.save(this.landscape, name);
            } else {
                System.err.println(name + ".txml existiert schon. Wähle einen anderen Namen aus!");
            }
        });
    }

    @FXML
    public void loadXML(ActionEvent event) {
        XMLSerialization serializator = new XMLSerialization();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Paths.get(XML_FOLDER).toFile());
        File xmlFile = fileChooser.showOpenDialog(pane.getScene().getWindow());

        if (xmlFile == null) {
            System.err.println("Keine TXML-Datei ausgewählt!");
            return;
        }

        String file = xmlFile.getName();
        String fileName = (file.contains(".txml")) ? file.split("\\.")[0] : null;

        if (fileName != null) {
            serializator.load(this, xmlFile, landscapePanel);
        } else {
            System.err.println("Die ausgewählte Datei ist keine gültige TXML-Datei!");
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
        } catch (FlagException | BaseException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void closeSimulation() {
        this.saveCode();
        Main.simulations.removeSimulation(editorClass);

        if (Main.simulations.isEmpty()) {
            Platform.exit();
        } else {
            this.stage.close();
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

    @FXML
    private void createNewSimulation() {
        Dialog<String> dialog = DialogProvider.getSingleTextBoxDialog("Neue Simulation starten",
                "Geben Sie einen gültigen Klassennamen ein", "Starten", "Name");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            if (Helper.isValidClassName(name)) {
                Path directory = Paths.get(Main.PROGAM_FOLDER, name + ".java");
                if (!Files.exists(directory)) {
                    try {
                        Files.createFile(directory);
                        this.createAndStartSimulation(name, null);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                } else {
                    System.err.println(name + ".java existiert schon. Öffne diese Simulation oder wähle einen anderen Namen!");
                }
            } else {
                System.err.println(name + "ist kein valider Klassenname!");
            }
        });


    }

    private void createAndStartSimulation(String name, String code) {
        Stage stage = new Stage();
        try {
            Main.createAndStartSimulation(stage, name, code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSimulation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Paths.get(Main.PROGAM_FOLDER).toFile());
        File simulationToOpen = fileChooser.showOpenDialog(pane.getScene().getWindow());

        if (simulationToOpen == null) {
            System.err.println("Du musst eine Officer-Klasse (.java) auswählen!");
            return;
        }

        String file = simulationToOpen.getName();
        String fileName = (file.contains(".java")) ? file.split("\\.")[0] : null;

        if (fileName != null && Main.simulations.constainsSimulation(fileName)) {
            System.err.println("Diese Simulation ist bereits geöffnet!");
        } else if (fileName != null && !Main.simulations.constainsSimulation(fileName)) {
            String code = null;
            try {
                String prefix = PREFIX_1 + fileName + PREFIX_2;
                code = new String(Files.readAllBytes(simulationToOpen.toPath()));
                code = code.replace(prefix, "");
                code = code.substring(0, code.length() - 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            createAndStartSimulation(fileName, code);

        } else {
            System.err.println("Die ausgewählte Datei ist keine gültige Java-Datei!");
        }


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
            this.itemToDrag = Field.POLICE_OFFICER;
        } else if (str.contains("baseMenu")) {
            baseButton.setSelected(true);
            this.moveBaseEnabled = true;
            this.itemToDrag = Field.BASE;
        } else if (str.contains("flagMenu")) {
            flagButton.setSelected(true);
            this.itemToAdd = Field.FLAG;
        } else if (str.contains("terroristUnarmedMenu")) {
            terrorUnarmedButton.setSelected(true);
            this.itemToAdd = Field.UNARMED_TERRORIST;
        } else if (str.contains("terroristArmedMenu")) {
            terrorArmedButton.setSelected(true);
            this.itemToAdd = Field.ARMED_TERRORIST;
        } else if (str.contains("fieldMenu")) {
            deleteButton.setSelected(true);
            this.landscape.setDeleteEnabled(true);
        }
    }

    private void disableStates(ToggleGroup addingGroup) {
        //if (isSimulationRunning()) return;

        this.moveAvatarEnabled = false;
        this.moveBaseEnabled = false;
        this.landscape.setDeleteEnabled(false);
        this.dragged = false;
        this.itemToAdd = Field.OUT_OF_FIELD;
        this.itemToDrag = Field.OUT_OF_FIELD;
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
            this.itemToDrag = Field.POLICE_OFFICER;
        } else if (str.contains("baseButton")) {
            baseButton.setSelected(true);
            this.moveBaseEnabled = true;
            this.itemToDrag = Field.BASE;
        } else if (str.contains("flagButton")) {
            flagMenu.setSelected(true);
            this.itemToAdd = Field.FLAG;
        } else if (str.contains("terrorUnarmedButton")) {
            terroristUnarmedMenu.setSelected(true);
            this.itemToAdd = Field.UNARMED_TERRORIST;
        } else if (str.contains("terrorArmedButton")) {
            terroristArmedMenu.setSelected(true);
            this.itemToAdd = Field.ARMED_TERRORIST;
        } else if (str.contains("deleteButton")) {
            fieldMenu.setSelected(true);
            this.landscape.setDeleteEnabled(true);
        }
    }
}
