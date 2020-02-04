package ctfmodell.serialization;

import ctfmodell.controller.Controller;
import ctfmodell.model.Landscape;
import ctfmodell.model.PoliceOfficer;
import ctfmodell.provider.DialogProvider;
import ctfmodell.view.LandscapePanel;
import javafx.scene.control.Alert;

import java.io.*;
import java.nio.file.Path;

/**
 * Klasse, die für die Serialisierung und Deserialisierung der Landschaft zuständig ist
 *
 * @author Nick Garbusa
 */
public class LandscapeSerialization {

    public void serialize(Landscape landscape, Path directory) {

        try {
            FileOutputStream fileOut =
                    new FileOutputStream(directory.toFile());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            landscape.setOfficerXPos(landscape.getPoliceOfficer().getxPos());
            landscape.setOfficerYPos(landscape.getPoliceOfficer().getyPos());
            landscape.setDirection(landscape.getPoliceOfficer().getDirection());
            landscape.setNumberOfFlags(landscape.getPoliceOfficer().getNumberOfFlags());
            out.writeObject(landscape);
            out.close();
            fileOut.close();

            DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Erfolgreich", "Speichern", "Landschaft wurde gespeichert!");

        } catch (IOException e) {
            System.err.println("[Simulation] " + e.getMessage());
        }

    }

    public void deserialize(Controller controller, File landscapeFile, LandscapePanel landscapePanel) {
        Landscape loadedLandscape;
        try {
            FileInputStream fileIn = new FileInputStream(landscapeFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            loadedLandscape = (Landscape) in.readObject();
            in.close();
            fileIn.close();


        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("[ERROR] Employee class not found");
            c.printStackTrace();
            return;
        }

        //Lade Landschaft ein
        PoliceOfficer policeOfficer = controller.landscape.getPoliceOfficer();
        policeOfficer.setyPos(loadedLandscape.getOfficerYPos());
        policeOfficer.setxPos(loadedLandscape.getOfficerXPos());
        policeOfficer.setDirection(loadedLandscape.getDirection());
        policeOfficer.setNumberOfFlags(loadedLandscape.getNumberOfFlags());

        controller.landscape = loadedLandscape;
        controller.deleteAndUpdateObserver();
        controller.landscape.reloadAfterDeserialization(policeOfficer);

        DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Erfolgreich", "Laden", "Landschaft wurde geladen!");

    }
}
