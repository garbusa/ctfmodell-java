package ctfmodell;

import ctfmodell.gui.LandscapePanel;
import ctfmodell.model.Landscape;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@SuppressWarnings("ConstantConditions")
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));
            primaryStage.setTitle("Capture The Flag");
            primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_with_flag.png")));

            for(Node n : root.getChildrenUnmodifiable()) {
                if (n instanceof SplitPane) {
                    SplitPane splitPane = (SplitPane) n;
                    ScrollPane scrollPane = new ScrollPane(new LandscapePanel(new Landscape(), 100, 100));
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
