package ctfmodell.provider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PropertyProvider {

    private static String fileName = "simulator.properties";
    private Properties simulationProperties;

    public PropertyProvider() {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            this.generateProperties(fis);
        } catch (IOException e) {
            System.err.println("IOException, wenn nicht existent -> generieren | sonst properties nachschauen");
        }
        this.checkAndGenerateProperties();

    }

    public String getRole() {
        return this.simulationProperties.getProperty("role");
    }

    public String getHost() {
        return this.simulationProperties.getProperty("tutorhost");
    }

    public String getPort() {
        return this.simulationProperties.getProperty("tutorport");
    }

    public String getLanguage() {
        String language = this.simulationProperties.getProperty("language");

        switch (language) {
            case "en":
                return "en";
            default:
                return "de";
        }
    }

    public void setLanguage(String language) {
        this.simulationProperties.setProperty("language", language);
    }

    private void checkAndGenerateProperties() {
        File file = new File(fileName);
        Path path = file.toPath();
        boolean exists = Files.exists(path);

        if (!exists) {
            try {
                Files.createFile(path);
                FileInputStream fis = new FileInputStream(fileName);
                this.generateProperties(fis);

            } catch (IOException e) {
                System.err.println("Fehler beim Erstellen aufgetreten");
            }
        }

        boolean hasRole = this.simulationProperties.containsKey("role");
        boolean hasHost = this.simulationProperties.containsKey("tutorhost");
        boolean hasPort = this.simulationProperties.containsKey("tutorport");
        boolean hasLanguage = this.simulationProperties.containsKey("language");

        this.completeProperties(hasRole, hasHost, hasPort, hasLanguage);
    }

    private void completeProperties(boolean hasRole, boolean hasHost, boolean hasPort, boolean hasLanguage) {
        if (!hasRole) {
            this.simulationProperties.setProperty("role", "student");
        }

        if (!hasHost) {
            this.simulationProperties.setProperty("tutorhost", "localhost");
        }

        if (!hasPort) {
            this.simulationProperties.setProperty("tutorport", "3579");
        }

        if (!hasLanguage) {
            this.simulationProperties.setProperty("language", "de");
        }

        File file = new File(fileName);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            this.simulationProperties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateProperties(FileInputStream fis) throws IOException {
        this.simulationProperties = new Properties();
        this.simulationProperties.load(fis);
        this.checkAndGenerateProperties();
    }
}
