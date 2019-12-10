package ctfmodell.serialization;

import ctfmodell.controller.Controller;
import ctfmodell.model.Landscape;
import ctfmodell.model.PoliceOfficer;
import ctfmodell.provider.DialogProvider;
import ctfmodell.view.LandscapePanel;
import javafx.scene.control.Alert;

import java.io.*;
import java.nio.file.Path;

public class LandscapeSerialization {

    public static void serialize(Landscape landscape, Path directory) {

        try {
            FileOutputStream fileOut =
                    new FileOutputStream(directory.toFile());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            landscape.setOfficerXPos(landscape.getPoliceOfficer().getxPos());
            landscape.setOfficerXPos(landscape.getPoliceOfficer().getyPos());
            landscape.setDirection(landscape.getPoliceOfficer().getDirection());
            landscape.setNumberOfFlags(landscape.getPoliceOfficer().getNumberOfFlags());
            out.writeObject(landscape);
            out.close();
            fileOut.close();

            DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Erfolgreich", "Speichern", "Landschaft wurde gespeichert!");

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public static void deserialize(Controller controller, File landscapeFile, LandscapePanel landscapePanel) {
        Landscape loadedLandscape = null;
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
            System.out.println("Employee class not found");
            c.printStackTrace();
            return;
        }

        //Lade Landschaft ein
        PoliceOfficer policeOfficer = controller.landscape.getPoliceOfficer();
        policeOfficer.setyPos(loadedLandscape.getOfficerYPos());
        policeOfficer.setxPos(loadedLandscape.getOfficerYPos());
        policeOfficer.setDirection(loadedLandscape.getDirection());
        policeOfficer.setNumberOfFlags(loadedLandscape.getNumberOfFlags());


        controller.landscape = loadedLandscape;
        controller.landscape.addObserver(landscapePanel);
        controller.landscape.reloadAfterDeserialization(policeOfficer);

        DialogProvider.alert(Alert.AlertType.CONFIRMATION, "Erfolgreich", "Laden", "Landschaft wurde geladen!");

    }
}
