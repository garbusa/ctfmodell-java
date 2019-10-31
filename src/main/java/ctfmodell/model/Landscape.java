package ctfmodell.model;

import ctfmodell.model.enums.FieldEnum;

import java.util.ArrayList;
import java.util.List;

public class Landscape {

    private PoliceOfficer policeOfficer;
    private int height;
    private int width;
    private int baseX;
    private int baseY;
    private FieldEnum[][] landscape;
    private List<Flag> flags;

    private Landscape() {

    }

    public Landscape(int height, int width, int baseX, int baseY) {
        this.height = height;
        this.width = width;

        this.baseX = baseX;
        this.baseY = baseY;

        this.landscape = this.generateFields();
        this.flags = new ArrayList<Flag>();
    }

    private FieldEnum[][] generateFields() {
        FieldEnum[][] fields  = new FieldEnum[this.height][this.width];
        for(int y = 0; y < fields.length; y++) {
            for(int x = 0; x < fields[y].length; x++) {
                if (x == this.baseX && y == this.baseY) {
                    fields[y][x] = FieldEnum.BASE;
                } else {
                    fields[y][x] = FieldEnum.EMPTY;
                }
            }
        }

        return fields;
    }

    public void addFlag(Flag flag) {
        //this.policeOfficer = policeOfficer;
        int x = flag.getxPos();
        int y = flag.getyPos();
        switch (this.landscape[y][x]) {
            case EMPTY:
                this.landscape[y][x] = FieldEnum.FLAG;
                this.flags.add(flag);
                break;
            case POLICE_OFFICER:
                this.landscape[y][x] = FieldEnum.OFFICER_AND_FLAG;
                this.flags.add(flag);
                break;
            default:
                System.out.println("Auf diesem Feld kann keine Flagge platziert werden platziert werden!");
                break;
        }
    }

    public void deleteFlag(Flag flag) {
        int x = flag.getxPos();
        int y = flag.getyPos();
        FieldEnum field = this.landscape[flag.getyPos()][flag.getxPos()];

        switch (field) {
            case FLAG:
                this.landscape[y][x] = FieldEnum.EMPTY;
                this.flags.remove(flag);
                break;
            case OFFICER_AND_FLAG:
                this.landscape[y][x] = FieldEnum.POLICE_OFFICER;
                this.flags.remove(flag);
                break;
            default:
                break;
        }

    }

    public void addUnarmedTerrorist(int y, int x) {
        if(this.landscape[y][x] == FieldEnum.EMPTY || this.landscape[y][x] == FieldEnum.ARMED_TERRORIST) {
            this.landscape[y][x] = FieldEnum.UNARMED_TERRORIST;
        } else {
            System.out.println("Hier kann kein Terrorist eingefügt werden!");
        }
    }

    public void addArmedTerrorist(int y, int x) {
        if(this.landscape[y][x] == FieldEnum.EMPTY || this.landscape[y][x] == FieldEnum.UNARMED_TERRORIST) {
            this.landscape[y][x] = FieldEnum.ARMED_TERRORIST;
        } else {
            System.out.println("Hier kann kein Terrorist eingefügt werden!");
        }
    }

    public PoliceOfficer getPoliceOfficer() {
        return policeOfficer;
    }

    public void setPoliceOfficer(PoliceOfficer policeOfficer) {
        this.policeOfficer = policeOfficer;
        int x = policeOfficer.getyPos();
        int y = policeOfficer.getxPos();
        switch (this.landscape[y][x]) {
            case EMPTY:
                this.landscape[y][x] = FieldEnum.POLICE_OFFICER;
                break;
            case FLAG:
                this.landscape[y][x] = FieldEnum.OFFICER_AND_FLAG;
                break;
            default:
                System.out.println("Auf diesem Feld kann kein Officer platziert werden!");
                break;
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        this.landscape = new FieldEnum[this.height][this.width];
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        this.landscape = new FieldEnum[this.height][this.width];
    }

    public FieldEnum[][] getLandscape() {
        return landscape;
    }

    public void setLandscape(FieldEnum[][] landscape) {
        this.landscape = landscape;
    }

    public boolean allFlagsPickedUp() {
        return this.policeOfficer.getNumberOfFlags() == this.flags.size();
    }

}
