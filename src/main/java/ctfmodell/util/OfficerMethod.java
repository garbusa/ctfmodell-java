package ctfmodell.util;

import java.util.ArrayList;
import java.util.List;

public class OfficerMethod {

    public enum Type {
        BASIC,
        NEW
    }

    private String methodType;
    private String methodName;
    private List<String> paramTypes;
    public OfficerMethod() {
        paramTypes = new ArrayList<>();
    }


    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() { return this.methodName; }

    public List<String> getParamTypes() {
        return paramTypes;
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

    public boolean hasParam() {
        return this.getParamTypes().size() > 0;
    }

    @Override
    public String toString() {
        return methodType + " " + methodName + paramTypesAsString();
    }
}
