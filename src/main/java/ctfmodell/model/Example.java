package ctfmodell.model;

import java.io.Serializable;

/**
 * Das Austausch Objekt für die Apache Derby Datenbank,
 * welches primär den Editorcode und die Landschaft
 * als XML-String hält
 *
 * @author Nick Garbusa
 */
public class Example implements Serializable {

    private int id;
    private String officerName;
    private String code;
    private String xml;

    Example() {

    }

    public Example(int id, String code, String xml, String officerName) {
        this.id = id;
        this.officerName = officerName;
        this.code = code;
        this.xml = xml;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public String toString() {
        return "Example{" +
                "id=" + id +
                ", officerName='" + officerName + '\'' +
                '}';
    }
}
