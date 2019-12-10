package ctfmodell.provider;

import ctfmodell.Main;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URISyntaxException;

public class SoundProvider {

    public static void beep() {
        Media sound = null;
        try {
            sound = new Media(
                    new File(Main.class.getClassLoader().getResource("beep.wav").toURI()).toURI().toString()
            );
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

}
