package ctfmodell.view;

import ctfmodell.model.annotation.Invisible;
import ctfmodell.util.OfficerMethod;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OfficerContextMenu extends ContextMenu {

    private List<OfficerMethod> basicMethods;
    private List<OfficerMethod> newMethods;

    public OfficerContextMenu() {
        super();
        this.basicMethods = new ArrayList<>();
        this.newMethods = new ArrayList<>();
        this.getBasicMethods();
    }

    private void getBasicMethods() {
        try {
            Class<?> officerClass = Class.forName("ctfmodell.model.PoliceOfficer");
            Method[] methods = officerClass.getDeclaredMethods();

            OfficerMethod methodToAdd;
            for (Method method : methods) {
                List<Annotation> annotations = Arrays.asList(method.getAnnotations());
                if (annotations.size() == 1 && annotations.get(0) instanceof Invisible) continue;
                methodToAdd = new OfficerMethod();
                String modifier = Modifier.toString(method.getModifiers());
                if (!modifier.equals("public")) continue;
                methodToAdd.setMethodType(method.getReturnType().toString());
                methodToAdd.setMethodName(method.getName());
                Class[] paramTypes = method.getParameterTypes();

                String paramType;
                for (Class paramType1 : paramTypes) {
                    paramType = paramType1.toString();
                    methodToAdd.getParamTypes().add(paramType);
                }
                basicMethods.add(methodToAdd);
            }

            for (OfficerMethod m : basicMethods) {
                this.getItems().add(new MenuItem(m.toString()));
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
