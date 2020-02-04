package ctfmodell.model;

import java.io.Serializable;

/**
 * Erbt von der {@link Example} Klasse und ist das Austausch-Objekt für
 * das Tutor-System. Wurde erweitert durch eine studentId
 *
 * @author Nick Garbusa
 */
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
