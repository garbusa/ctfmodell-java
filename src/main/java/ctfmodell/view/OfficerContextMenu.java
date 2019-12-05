package ctfmodell.view;

import ctfmodell.model.Landscape;
import ctfmodell.model.PoliceOfficer;
import ctfmodell.model.annotation.Invisible;
import ctfmodell.util.BeepHelper;
import ctfmodell.util.OfficerMethod;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class OfficerContextMenu extends ContextMenu {

    private List<OfficerMethod> basicMethods;
    private List<OfficerMethod> newMethods;
    private Landscape landscape;

    public OfficerContextMenu(String officerType, Landscape landscape) {
        super();
        this.landscape = landscape;
        this.basicMethods = new ArrayList<>();
        this.newMethods = new ArrayList<>();
        this.getBasicMethods(officerType);
        if (!officerType.equals("ctfmodell.model.PoliceOfficer")) {
            this.getNewMethods(officerType);
        }
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
        PoliceOfficer officer = this.landscape.getPoliceOfficer();
            Method[] methods = officer.getClass().getDeclaredMethods();
            reflectAndAddMethods(methods, OfficerMethod.Type.NEW, officerType);
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
        PoliceOfficer officer = this.landscape.getPoliceOfficer();

        Method methodToExecute = null;
        for (OfficerMethod m : list) {
            menuItem = new MenuItem(m.toString());

            try {
                if (type == OfficerMethod.Type.BASIC && !m.hasParam() && !officerType.equals("ctfmodell.model.PoliceOfficer")) {
                    methodToExecute = officer.getClass().getSuperclass().getDeclaredMethod(m.getMethodName());
                } else if (type == OfficerMethod.Type.BASIC && !m.hasParam() && officerType.equals("ctfmodell.model.PoliceOfficer")) {
                    methodToExecute = officer.getClass().getDeclaredMethod(m.getMethodName());
                } else if (type == OfficerMethod.Type.NEW && !m.hasParam()) {
                    methodToExecute = officer.getClass().getDeclaredMethod(m.getMethodName());
                } else if (m.hasParam()) {
                    menuItem.setDisable(true);
                    this.getItems().add(menuItem);
                    continue;
                } else {
                    continue;
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }


            Method finalMethodToExecute = methodToExecute;
            menuItem.setOnAction((event) -> {
                try {
                    finalMethodToExecute.invoke(this.landscape.getPoliceOfficer());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.err.println(e.getCause().getMessage());
                    BeepHelper.beep();
                }
            });
            this.getItems().add(menuItem);
        }
    }



}

