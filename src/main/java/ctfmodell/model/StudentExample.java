package ctfmodell.model;

import java.io.Serializable;

public class StudentExample extends Example implements Serializable {

    private String studentId;

    public StudentExample() {

    }

    public StudentExample(String studentId, String code, String xml) {
        super(-1, code, xml, "");
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
