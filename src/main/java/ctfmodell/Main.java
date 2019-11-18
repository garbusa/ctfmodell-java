package ctfmodell;

import ctfmodell.controller.Controller;
import ctfmodell.model.Landscape;
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

@SuppressWarnings("ConstantConditions")
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Parent root = loader.load();

        //Add Observers
        Landscape landscape = new Landscape();
        LandscapePanel landscapePanel = new LandscapePanel(landscape);
        landscape.addObserver(landscapePanel);
        landscape.getPoliceOfficer().addObserver(landscapePanel);

        Controller controller = loader.getController();
        controller.initialize(landscape, landscapePanel);
        controller.initializeEventHandler();

        //Add LandscapePanel to GUI
        SplitPane splitPane = (SplitPane) ((BorderPane) root).getCenter();
        VBox vBox = (VBox) splitPane.getItems().get(1);
        HBox hBox = (HBox) vBox.getChildren().get(0);

        ScrollPane scrollPane = (ScrollPane) hBox.getChildren().get(0);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(landscapePanel);

        primaryStage.setTitle("Capture The Flag");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_with_flag.png")));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
