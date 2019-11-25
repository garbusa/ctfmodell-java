package ctfmodell.view;

import ctfmodell.Main;
import ctfmodell.model.Landscape;
import ctfmodell.model.PoliceOfficer;
import ctfmodell.model.annotation.Invisible;
import ctfmodell.util.OfficerMethod;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class OfficerContextMenu extends ContextMenu implements Observer {

    private List<OfficerMethod> basicMethods;
    private List<OfficerMethod> newMethods;
    private Landscape landscape;

    public OfficerContextMenu(String officerType) {
        super();
        this.basicMethods = new ArrayList<>();
        this.newMethods = new ArrayList<>();
        this.getBasicMethods(officerType);
        if (!officerType.equals("ctfmodell.model.PoliceOfficer")) {
            this.getNewMethods(officerType);
        }
    }

    public void setLandscape(Landscape landscape) {
        this.landscape = landscape;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.landscape = (Landscape) o;
    }

    private void getBasicMethods(String officerType) {
        try {
            Class<?> officerClass = Class.forName("ctfmodell.model.PoliceOfficer");
            Method[] methods = officerClass.getDeclaredMethods();

            reflectAndAddMethods(methods, OfficerMethod.Type.BASIC, officerType);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getNewMethods(String officerType) {
        try {
            PoliceOfficer officer = this.getActualOfficer(officerType);
            Method[] methods = officer.getClass().getDeclaredMethods();
            reflectAndAddMethods(methods, OfficerMethod.Type.NEW, officerType);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }

    }

    private void reflectAndAddMethods(Method[] methods, OfficerMethod.Type type, String officerType) {
        OfficerMethod methodToAdd;
        for (Method method : methods) {
            List<Annotation> annotations = Arrays.asList(method.getAnnotations());
            String modifier = Modifier.toString(method.getModifiers());
            if (annotations.size() == 1 && annotations.get(0) instanceof Invisible
                    || !modifier.equals("public")) continue;
            methodToAdd = new OfficerMethod();
            methodToAdd.setMethodType(method.getReturnType().toString());
            methodToAdd.setMethodName(method.getName());
            Class[] paramTypes = method.getParameterTypes();

            String paramType;
            for (Class paramType1 : paramTypes) {
                paramType = paramType1.toString();
                methodToAdd.getParamTypes().add(paramType);
            }

            if (type == OfficerMethod.Type.BASIC) {
                basicMethods.add(methodToAdd);
            } else {
                newMethods.add(methodToAdd);
            }

        }

        if (type == OfficerMethod.Type.BASIC) {
            addMethodsWithEvent(OfficerMethod.Type.BASIC, officerType);
        } else {
            addMethodsWithEvent(OfficerMethod.Type.NEW, officerType);
        }

    }

    private void addMethodsWithEvent(OfficerMethod.Type type, String officerType) {
        MenuItem menuItem;
        List<OfficerMethod> list = (type == OfficerMethod.Type.BASIC) ? this.basicMethods : this.newMethods;
        PoliceOfficer officer = null;

        try {
            officer = this.getActualOfficer(officerType);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            System.err.println(e.getMessage());
        }

        Method methodToExecute = null;
        for (OfficerMethod m : list) {
            menuItem = new MenuItem(m.toString());
            this.getItems().add(menuItem);

            try {
                if (type == OfficerMethod.Type.BASIC && !m.hasParam() && !officerType.equals("ctfmodell.model.PoliceOfficer")) {
                    methodToExecute = officer.getClass().getSuperclass().getDeclaredMethod(m.getMethodName());
                } else if (type == OfficerMethod.Type.BASIC && !m.hasParam() && officerType.equals("ctfmodell.model.PoliceOfficer")) {
                    methodToExecute = officer.getClass().getDeclaredMethod(m.getMethodName());
                } else if (type == OfficerMethod.Type.NEW && !m.hasParam()) {
                    methodToExecute = officer.getClass().getDeclaredMethod(m.getMethodName());
                } else {
                    continue;
                }

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            if (m.hasParam()) {
                menuItem.setDisable(true);
            } else {
                Method finalMethodToExecute = methodToExecute;
                menuItem.setOnAction((event) -> {
                    try {
                        finalMethodToExecute.invoke(this.landscape.getPoliceOfficer());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        System.err.println(e.getCause().getMessage());
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
                });
            }
        }
    }

    private PoliceOfficer getActualOfficer(String officerType) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Path programsFolder = Paths.get(Main.PROGAM_FOLDER);
        ClassLoader cl = getClassLoader(programsFolder);

        if (cl == null) {
            System.err.println("Classloader konnte nicht geladen werden!");
            return null;
        }
        return (PoliceOfficer) cl.loadClass(officerType).newInstance();
    }

    private ClassLoader getClassLoader(Path programsFolder) {
        ClassLoader cl = null;
        try {
            URL[] urls = new URL[]{programsFolder.toFile().toURI().toURL()};
            cl = new URLClassLoader(urls);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        return cl;
    }


}

