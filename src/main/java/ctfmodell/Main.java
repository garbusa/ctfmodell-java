package ctfmodell;

import ctfmodell.gui.LandscapePanel;
import ctfmodell.model.Flag;
import ctfmodell.model.Landscape;
import ctfmodell.model.PoliceOfficer;
import ctfmodell.model.enums.DirectionEnum;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@SuppressWarnings("ConstantConditions")
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));
        primaryStage.setTitle("Capture The Flag");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_with_flag.png")));


        Landscape landscape = new Landscape(10, 10, 7, 4);
        PoliceOfficer policeOfficer = new PoliceOfficer(0, 0, DirectionEnum.EAST);
        policeOfficer.setLandscape(landscape);
        landscape.setPoliceOfficer(policeOfficer);
        Flag flagOne = new Flag(2, 2);
        Flag flagTwo = new Flag(3, 3);
        Flag flagThree = new Flag(4, 4);
        Flag flagFour = new Flag(0, 0);

        landscape.addFlag(flagOne);
        landscape.addFlag(flagTwo);
        landscape.addFlag(flagThree);
        landscape.addFlag(flagFour);
        landscape.addUnarmedTerrorist(0, 1);
        landscape.addArmedTerrorist(1, 1);
        landscape.addArmedTerrorist(1, 0);

        for (Node n : root.getChildrenUnmodifiable()) {
            if (n instanceof SplitPane) {
                SplitPane splitPane = (SplitPane) n;
                ScrollPane scrollPane = new ScrollPane(new LandscapePanel(landscape, 100, 100));
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                splitPane.getItems().add(scrollPane);
            }
        }

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
