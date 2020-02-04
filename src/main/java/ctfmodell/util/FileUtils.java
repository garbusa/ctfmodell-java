package ctfmodell.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static final String PROGAM_FOLDER = "programs";
    public static final String LANDSCAPE_FOLDER = "landscapes";
    public static final String XML_FOLDER = "xml";
    public static final String PREFIX_1 = "public class ";
    public static final String PREFIX_2 = " extends ctfmodell.model.PoliceOfficer {\n\npublic ";
    public static final String POSTFIX = "\n\n}";

    public static void createProgramFolder() {
        Path directory = Paths.get(PROGAM_FOLDER);

        if (!Files.exists(directory)) {
            try {
                Files.createDirectory(directory);
            } catch (IOException e) {
                System.out.println("[ERROR] " + e.getMessage());
            } finally {
                createDefaultOfficer();
            }
        } else {
            System.out.println("[DEBUG] Programm-Verzeichnis existiert schon!");
            createDefaultOfficer();
        }

    }

    private static void createDefaultOfficer() {
        Path directory = Paths.get(PROGAM_FOLDER, "DefaultOfficer.java");

        if (!Files.exists(directory)) {
            try {
                Files.createFile(directory);
            } catch (IOException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        } else {
            System.out.println("[DEBUG] DefaultOfficer.java gibt es schon existiert schon!");
        }
    }

    public static void createSerializationFolder() {
        Path directory = Paths.get(LANDSCAPE_FOLDER);

        if (!Files.exists(directory)) {
            try {
                Files.createDirectory(directory);
            } catch (IOException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        } else {
            System.out.println("[DEBUG] LANDSCAPE_FOLDER-Verzeichnis existiert schon!");
        }

    }

    public static void createXMLFolder() {
        Path directory = Paths.get(XML_FOLDER);

        if (!Files.exists(directory)) {
            try {
                Files.createDirectory(directory);
            } catch (IOException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        } else {
            System.out.println("[DEBUG] XML-Verzeichnis existiert schon!");
        }

    }

    public static String loadDefaultOfficerCode() {
        Path directory = Paths.get(PROGAM_FOLDER, "DefaultOfficer.java");

        if (Files.exists(directory)) {
            try {
                String prefix = PREFIX_1 + "DefaultOfficer" + PREFIX_2;
                String code = new String(Files.readAllBytes(directory));
                if (code.length() < 1) return null;
                code = code.replace(prefix, "");
                code = code.substring(0, code.length() - 1);
                return code;
            } catch (IOException e) {
                System.out.println("[ERROR] " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }


}
