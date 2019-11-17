package ctfmodell.model;

import ctfmodell.model.enums.DirectionEnum;
import ctfmodell.model.enums.FieldEnum;
import ctfmodell.model.exception.LandscapeException;

import java.lang.reflect.Field;
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

    public Landscape() {
        this.height = 10;
        this.width = 10;
        this.baseX = 9;
        this.baseY = 9;

        this.landscape = this.generateFields();
        this.flags = new ArrayList<>();

        PoliceOfficer policeOfficer = new PoliceOfficer(0, 0, DirectionEnum.EAST);
        policeOfficer.setLandscape(this);
        this.setPoliceOfficer(policeOfficer);
        Flag flagOne = new Flag(2, 2);
        Flag flagTwo = new Flag(3, 3);

        this.addFlag(flagOne);
        this.addFlag(flagTwo);
        this.addUnarmedTerrorist(0, 1);
        this.addArmedTerrorist(1, 1);
        this.addArmedTerrorist(1, 0);
    }

    public Landscape(int height, int width, int baseX, int baseY, PoliceOfficer policeOfficer) {
        this.height = height;
        this.width = width;

        this.baseX = baseX;
        this.baseY = baseY;

        this.landscape = this.generateFields();
        this.flags = new ArrayList<>();

        policeOfficer.setLandscape(this);
        this.setPoliceOfficer(policeOfficer);
    }

    private FieldEnum[][] generateFields() {
        FieldEnum[][] fields = new FieldEnum[this.height][this.width];
        for (int y = 0; y < fields.length; y++) {
            for (int x = 0; x < fields[y].length; x++) {
                if (x == this.baseX && y == this.baseY) {
                    fields[y][x] = FieldEnum.BASE;
                } else {
                    fields[y][x] = FieldEnum.EMPTY;
                }
            }
        }

        return fields;
    }

    /* **************************************** ADD & DELETE ******************************************* */

    public void addFlag(Flag flag) {
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
                throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann keine Flagge platziert werden!", y, x));
        }
    }

    public void deleteFlag(Flag flag) {
        int x = flag.getxPos();
        int y = flag.getyPos();
        FieldEnum field = this.landscape[flag.getyPos()][flag.getxPos()];

        if(!this.isNotEndOfField(y, x)) {
            throw new LandscapeException("Es wurde versuch eine Flagge aus einem nicht existierenden Feld zu löschen!");
        }

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
                throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann keine Flagge entfernt werden!", y, x));
        }

    }

    public void addUnarmedTerrorist(int y, int x) {
        if (this.landscape[y][x] == FieldEnum.EMPTY || this.landscape[y][x] == FieldEnum.ARMED_TERRORIST) {
            this.landscape[y][x] = FieldEnum.UNARMED_TERRORIST;
        } else {
            throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann kein Terrorist platziert werden!", y, x));
        }
    }

    public void deleteUnarmedTerrorist(int y, int x) {
        if(!this.isNotEndOfField(y, x)) {
            throw new LandscapeException("Es wurde versuch ein Terrorist aus einem nicht existierenden Feld zu löschen!");
        } else if(this.landscape[y][x] == FieldEnum.UNARMED_TERRORIST) {
            this.landscape[y][x] = FieldEnum.EMPTY;
        } else {
            throw new LandscapeException("Auf diesem Feld befindet sich kein Terrorist!");
        }
    }

    public void addArmedTerrorist(int y, int x) {
        if (this.landscape[y][x] == FieldEnum.EMPTY || this.landscape[y][x] == FieldEnum.UNARMED_TERRORIST) {
            this.landscape[y][x] = FieldEnum.ARMED_TERRORIST;
        } else {
            throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann kein Terrorist platziert werden!", y, x));
        }
    }

    public void deleteArmedTerrorist(int y, int x) {
        if(!this.isNotEndOfField(y, x)) {
            throw new LandscapeException("Es wurde versuch ein Terrorist aus einem nicht existierenden Feld zu löschen!");
        } else if(this.landscape[y][x] == FieldEnum.ARMED_TERRORIST) {
            this.landscape[y][x] = FieldEnum.EMPTY;
        } else {
            throw new LandscapeException("Auf diesem Feld befindet sich kein Terrorist!");
        }
    }

    /* **************************************** HELPER ******************************************* */

    protected boolean isNotEndOfField(int y, int x) {
        return (y > -1 && y < this.landscape.length)
                && (x > -1 && x < this.landscape[y].length);
    }

    private boolean hasPoliceOfficerOnLandscape(FieldEnum[][] landscape) {
        for(int y = 0; y < landscape.length; y++) {
            for(int x = 0; x < landscape[y].length; x++) {
                if(landscape[y][x] == FieldEnum.OFFICER_AND_FLAG ||
                        landscape[y][x] == FieldEnum.OFFICER_AND_BASE ||
                        landscape[y][x] == FieldEnum.POLICE_OFFICER) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasBaseOnLandscape(FieldEnum[][] landscape) {
        for(int y = 0; y < landscape.length; y++) {
            for(int x = 0; x < landscape[y].length; x++) {
                if(landscape[y][x] == FieldEnum.OFFICER_AND_BASE ||
                        landscape[y][x] == FieldEnum.BASE) {
                    return true;
                }
            }
        }
        return false;
    }

    private FieldEnum getFieldEnum(int y, int x) {
        return this.landscape[y][x];
    }

    private boolean hasOfficer() {
        return this.policeOfficer != null;
    }

    /* **************************************** GETTER & SETTER ******************************************* */

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
            case BASE:
                this.landscape[y][x] = FieldEnum.OFFICER_AND_BASE;
                break;
            default:
                throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann kein Officer platziert werden!", y, x));
        }
    }

    public void unsetPoliceOfficer() {
        int x = policeOfficer.getyPos();
        int y = policeOfficer.getxPos();
        this.policeOfficer = null;
        switch (this.landscape[y][x]) {
            case POLICE_OFFICER:
                this.landscape[y][x] = FieldEnum.EMPTY;
                break;
            case OFFICER_AND_FLAG:
                this.landscape[y][x] = FieldEnum.FLAG;
                break;
            case OFFICER_AND_BASE:
                this.landscape[y][x] = FieldEnum.BASE;
                break;
            default:
                throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann kein Officer platziert werden!", y, x));
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void resize(int width, int height) {
        if(this.landscape == null) throw new LandscapeException("Du kannst nicht die Größe einer nicht existierenden Landschaft verändern!");
        if(width < 2 || height < 2) throw new LandscapeException("Eine neue Landschaft muss mindestens 2x2 groß sein!");

        this.width = width;
        this.height = height;
        FieldEnum[][] resizedLandscape = new FieldEnum[height][width];

        //Kopiere alle möglichen Felder von der alten zur neuen
        for(int y = 0; y < resizedLandscape.length; y++) {
            if (y >= this.landscape.length) break;
            for (int x = 0; x < resizedLandscape[y].length; x++) {
                if (x >= this.landscape[y].length) break;
                resizedLandscape[y][x] = this.landscape[y][x];
            }
        }

        for(int y = 0; y < resizedLandscape.length; y++) {
            for (int x = 0; x < resizedLandscape[y].length; x++) {
                if (resizedLandscape[y][x] == null) {
                    resizedLandscape[y][x] = FieldEnum.EMPTY;
                }
            }
        }

        //Falls das vorherige Feld einen Akteur und oder eine Base hatte aber nicht zum neuen übernommen wurde,
        //Füge Officer hinzu
        if(hasPoliceOfficerOnLandscape(this.landscape) && !hasPoliceOfficerOnLandscape(resizedLandscape)){
            FieldEnum fieldToCheck = getFieldEnum(0, 0);
            if(fieldToCheck == FieldEnum.BASE) resizedLandscape[0][0] = FieldEnum.OFFICER_AND_BASE;
            else if(fieldToCheck == FieldEnum.FLAG) resizedLandscape[0][0] = FieldEnum.OFFICER_AND_FLAG;
            else resizedLandscape[0][0] = FieldEnum.POLICE_OFFICER;
        }

        if(hasBaseOnLandscape(this.landscape) && !hasBaseOnLandscape(resizedLandscape)){
            FieldEnum fieldToCheck = getFieldEnum(resizedLandscape.length, resizedLandscape[0].length);
            if(fieldToCheck == FieldEnum.POLICE_OFFICER) resizedLandscape[0][0] = FieldEnum.OFFICER_AND_BASE;
            else resizedLandscape[height-1][width-1] = FieldEnum.BASE;
        }

        this.landscape = resizedLandscape;

    }

    public FieldEnum[][] getLandscape() {
        return landscape;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }
}
