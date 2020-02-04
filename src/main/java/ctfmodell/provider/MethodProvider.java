package ctfmodell.provider;

import java.util.ArrayList;
import java.util.List;

/**
 * Helferklasse, um die Methoden des Akteurs darstellen zu können (im ContextMenu)
 *
 * @author Nick Garbusa
 */
public class MethodProvider {

    private String methodType;
    private String methodName;
    private List<String> paramTypes;

    public MethodProvider() {
        paramTypes = new ArrayList<>();
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean hasParam() {
        return this.getParamTypes().size() > 0;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    @Override
    public String toString() {
        return methodType + " " + methodName + paramTypesAsString();
    }

    private String paramTypesAsString() {
        StringBuilder str = new StringBuilder("(");

        int i = 0;
        for (String param : paramTypes) {
            str.append(param);

            if (i < paramTypes.size() - 1) {
                str.append(", ");
            }
            i++;
        }
        str.append(");");

        return str.toString();
    }

    public enum Type {
        BASIC,
        NEW
    }
}
