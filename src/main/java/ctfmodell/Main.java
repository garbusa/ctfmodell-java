package ctfmodell;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@SuppressWarnings("ConstantConditions")
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));
            primaryStage.setTitle("Capture The Flag");
            primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_with_flag.png")));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
