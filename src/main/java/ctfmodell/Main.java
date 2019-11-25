package ctfmodell;

import ctfmodell.container.SimulationContainer;
import ctfmodell.controller.Controller;
import ctfmodell.model.Landscape;
import ctfmodell.util.Helper;
import ctfmodell.view.LandscapePanel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("ConstantConditions")
public class Main extends Application {

    public static final String PROGAM_FOLDER = "programs";
    public static final String PREFIX_1 = "public class ";
    public static final String PREFIX_2 = " extends ctfmodell.model.PoliceOfficer {\n\npublic ";
    public static final String POSTFIX = "\n\n}";
    public static SimulationContainer simulations = new SimulationContainer();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        createProgramFolder();
        String defaultCode = loadDefaultOfficerCode();
        createAndStartSimulation(primaryStage, "DefaultOfficer", defaultCode);
    }

    private static void createProgramFolder() {
        Path directory = Paths.get(PROGAM_FOLDER);

        if (!Files.exists(directory)) {
            try {
                Files.createDirectory(directory);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                createDefaultOfficer();
            }
        } else {
            System.err.println("Programm-Verzeichnis existiert schon!");
            createDefaultOfficer();
        }

    }

    private static String loadDefaultOfficerCode() {
        Path directory = Paths.get(PROGAM_FOLDER, "DefaultOfficer.java");

        if (Files.exists(directory)) {
            try {
                String prefix = PREFIX_1 + "DefaultOfficer" + PREFIX_2;
                String code = new String(Files.readAllBytes(directory));
                if(code.length() < 1) return null;
                code = code.replace(prefix, "");
                code = code.substring(0, code.length() - 1);
                return code;
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public static void createAndStartSimulation(Stage primaryStage, String editorClass, String code) throws IOException {
        if (!Helper.isValidClassName(editorClass)) {
            System.err.println(editorClass + "ist kein valider Klassenname!");
            System.exit(0);
        }

        if (code == null) {
            code = "void main() {\n\n}";
        }


        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("main.fxml"));
        Parent root = loader.load();

        //Add Observers
        Landscape landscape = new Landscape();
        LandscapePanel landscapePanel = new LandscapePanel(landscape);
        landscape.addObserver(landscapePanel);


        Controller controller = loader.getController();
        controller.setEditorClass(editorClass);
        controller.initialize(landscape, landscapePanel, code);
        controller.initializeEventHandler();

        //Add LandscapePanel to GUI
        SplitPane splitPane = (SplitPane) ((BorderPane) root).getCenter();
        VBox vBox = (VBox) splitPane.getItems().get(1);
        HBox hBox = (HBox) vBox.getChildren().get(0);

        ScrollPane scrollPane = (ScrollPane) hBox.getChildren().get(0);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(landscapePanel);

        primaryStage.setTitle(editorClass);
        primaryStage.getIcons().add(new Image(Main.class.getClassLoader().getResourceAsStream("image/menu/police_with_flag.png")));
        primaryStage.setScene(new Scene(root));
        simulations.addSimulation(editorClass);

        primaryStage.setOnCloseRequest((event) -> {
            controller.saveCode();
            simulations.removeSimulation(editorClass);
        });

        controller.setStage(primaryStage);
        controller.saveCode();

        primaryStage.show();
    }

    private static void createDefaultOfficer() {
        Path directory = Paths.get(PROGAM_FOLDER, "DefaultOfficer.java");

        if (!Files.exists(directory)) {
            try {
                Files.createFile(directory);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.err.println("DefaultOfficer.java gibt es schon existiert schon!");
        }
    }


}
