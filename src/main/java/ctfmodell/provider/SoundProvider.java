package ctfmodell.provider;

import ctfmodell.Main;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Beinhaltet statische Methode, die ein Error-Sound wiedergibt
 *
 * @author Nick Garbusa
 */
public class SoundProvider {

    public static void beep() {
        Media sound = null;
        try {
            sound = new Media(
                    new File(Objects.requireNonNull(Main.class.getClassLoader().getResource("beep.wav")).toURI()).toURI().toString()
            );
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        assert sound != null;
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

}
