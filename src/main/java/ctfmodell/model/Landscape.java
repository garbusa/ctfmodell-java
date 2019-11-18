package ctfmodell.model;

import ctfmodell.model.enums.FieldEnum;
import ctfmodell.model.exception.LandscapeException;
import ctfmodell.util.Coordinates;
import ctfmodell.util.GraphicSize;
import ctfmodell.util.PixelRectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Landscape extends Observable {

    private PoliceOfficer policeOfficer;
    private int height;
    private int width;
    private int baseX;
    private int baseY;
    private FieldEnum[][] landscape;
    private List<Flag> flags;
    private PixelRectangle[][] landscapeCoordinates;

    public Landscape() {
        this.height = 10;
        this.width = 10;
        this.baseX = 9;
        this.baseY = 9;

        this.landscape = this.generateFields();
        this.flags = new ArrayList<>();
        this.landscapeCoordinates = new PixelRectangle[landscape.length][landscape[0].length];
        this.regeneratePixelLandscape();

        PoliceOfficer policeOfficer = new PoliceOfficer();
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

    private void regeneratePixelLandscape() {
        int posX = 0;
        int posY = 0;
        for (int y = 0; y < this.landscape.length; y++) {
            for (int x = 0; x < this.landscape[y].length; x++) {
                landscapeCoordinates[y][x] = new PixelRectangle(posY, posY + GraphicSize.RECT_SIZE, posX, posX + GraphicSize.RECT_SIZE);
                posX += GraphicSize.RECT_SIZE + GraphicSize.GAP_SIZE;
            }
            posX = 0;
            posY += GraphicSize.RECT_SIZE + GraphicSize.GAP_SIZE;

        }
    }

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
        this.setChanged();
        this.notifyObservers();
    }

    public void addUnarmedTerrorist(int y, int x) {
        if (this.landscape[y][x] == FieldEnum.EMPTY || this.landscape[y][x] == FieldEnum.ARMED_TERRORIST) {
            this.landscape[y][x] = FieldEnum.UNARMED_TERRORIST;
        } else {
            throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann kein Terrorist platziert werden!", y, x));
        }
        this.setChanged();
        this.notifyObservers();
    }

    public void addArmedTerrorist(int y, int x) {
        if (this.landscape[y][x] == FieldEnum.EMPTY || this.landscape[y][x] == FieldEnum.UNARMED_TERRORIST) {
            this.landscape[y][x] = FieldEnum.ARMED_TERRORIST;
        } else {
            throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann kein Terrorist platziert werden!", y, x));
        }
        this.setChanged();
        this.notifyObservers();
    }

    public void updatePoliceOfficer(PoliceOfficer policeOfficer) {
        this.policeOfficer = policeOfficer;
    }

    public void resize(int width, int height) {
        if (this.landscape == null)
            throw new LandscapeException("Du kannst nicht die Größe einer nicht existierenden Landschaft verändern!");
        if (width < 2 || height < 2)
            throw new LandscapeException("Eine neue Landschaft muss mindestens 2x2 groß sein!");

        this.width = width;
        this.height = height;
        FieldEnum[][] resizedLandscape = new FieldEnum[height][width];
        landscapeCoordinates = new PixelRectangle[height][width];

        //Kopiere alle möglichen Felder von der alten zur neuen
        for (int y = 0; y < resizedLandscape.length; y++) {
            if (y >= this.landscape.length) break;
            for (int x = 0; x < resizedLandscape[y].length; x++) {
                if (x >= this.landscape[y].length) break;
                resizedLandscape[y][x] = this.landscape[y][x];
            }
        }

        for (int y = 0; y < resizedLandscape.length; y++) {
            for (int x = 0; x < resizedLandscape[y].length; x++) {
                if (resizedLandscape[y][x] == null) {
                    resizedLandscape[y][x] = FieldEnum.EMPTY;
                }
            }
        }

        //Falls das vorherige Feld einen Akteur und oder eine Base hatte aber nicht zum neuen übernommen wurde,
        //Füge Officer hinzu
        if (hasPoliceOfficerOnLandscape(this.landscape) && !hasPoliceOfficerOnLandscape(resizedLandscape)) {
            FieldEnum fieldToCheck = getFieldEnum(0, 0);
            if (fieldToCheck == FieldEnum.BASE) resizedLandscape[0][0] = FieldEnum.OFFICER_AND_BASE;
            else if (fieldToCheck == FieldEnum.FLAG) resizedLandscape[0][0] = FieldEnum.OFFICER_AND_FLAG;
            else resizedLandscape[0][0] = FieldEnum.POLICE_OFFICER;
        }

        if (hasBaseOnLandscape(this.landscape) && !hasBaseOnLandscape(resizedLandscape)) {
            FieldEnum fieldToCheck = getFieldEnum(resizedLandscape.length, resizedLandscape[0].length);
            if (fieldToCheck == FieldEnum.POLICE_OFFICER) resizedLandscape[0][0] = FieldEnum.OFFICER_AND_BASE;
            else resizedLandscape[height - 1][width - 1] = FieldEnum.BASE;
        }

        this.landscape = resizedLandscape;
        this.regeneratePixelLandscape();
        this.setChanged();
        this.notifyObservers();
    }

    private boolean hasPoliceOfficerOnLandscape(FieldEnum[][] landscape) {
        for (FieldEnum[] fieldEnums : landscape) {
            for (FieldEnum fieldEnum : fieldEnums) {
                if (fieldEnum == FieldEnum.OFFICER_AND_FLAG ||
                        fieldEnum == FieldEnum.OFFICER_AND_BASE ||
                        fieldEnum == FieldEnum.POLICE_OFFICER) {
                    return true;
                }
            }
        }
        return false;
    }

    private FieldEnum getFieldEnum(int y, int x) {
        return this.landscape[y][x];
    }

    private boolean hasBaseOnLandscape(FieldEnum[][] landscape) {
        for (FieldEnum[] fieldEnums : landscape) {
            for (FieldEnum fieldEnum : fieldEnums) {
                if (fieldEnum == FieldEnum.OFFICER_AND_BASE ||
                        fieldEnum == FieldEnum.BASE) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Coordinates getFieldByCoordinates(int yPos, int xPos) {
        for (int y = 0; y < landscapeCoordinates.length; y++) {
            for (int x = 0; x < landscapeCoordinates[y].length; x++) {
                if (landscapeCoordinates[y][x].isValidPixel(yPos, xPos)) {
                    return new Coordinates(y, x);
                }
            }
        }

        return null;
    }

    public void clearOriginPolice(Integer y, Integer x) {
        FieldEnum field = this.landscape[y][x];
        switch (field) {
            case OFFICER_AND_BASE:
                this.landscape[y][x] = FieldEnum.BASE;
                break;
            case OFFICER_AND_FLAG:
                this.landscape[y][x] = FieldEnum.FLAG;
                break;
            case POLICE_OFFICER:
                this.landscape[y][x] = FieldEnum.EMPTY;
        }
        this.setChanged();
        this.notifyObservers();
    }

    public void setDestinationPolice(Integer y, Integer x) {
        FieldEnum field = this.landscape[y][x];
        this.getPoliceOfficer().setyPos(y);
        this.getPoliceOfficer().setxPos(x);
        switch (field) {
            case EMPTY:
                this.landscape[y][x] = FieldEnum.POLICE_OFFICER;
                break;
            case FLAG:
                this.landscape[y][x] = FieldEnum.OFFICER_AND_FLAG;
                break;
            case BASE:
                this.landscape[y][x] = FieldEnum.OFFICER_AND_BASE;
        }
        this.setChanged();
        this.notifyObservers();
    }

    public PoliceOfficer getPoliceOfficer() {
        return policeOfficer;
    }

    private void setPoliceOfficer(PoliceOfficer policeOfficer) {
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
        this.setChanged();
        this.notifyObservers();
    }

    public void clearOriginBase(Integer y, Integer x) {
        FieldEnum field = this.landscape[y][x];
        switch (field) {
            case OFFICER_AND_BASE:
                this.landscape[y][x] = FieldEnum.POLICE_OFFICER;
                break;
            case BASE:
                this.landscape[y][x] = FieldEnum.EMPTY;
                break;
        }
        this.setChanged();
        this.notifyObservers();
    }

    public void setDestinationBase(Integer y, Integer x) {
        FieldEnum field = this.landscape[y][x];
        this.getPoliceOfficer().setyPos(y);
        this.getPoliceOfficer().setxPos(x);
        switch (field) {
            case EMPTY:
                this.landscape[y][x] = FieldEnum.BASE;
                break;
            case POLICE_OFFICER:
                this.landscape[y][x] = FieldEnum.OFFICER_AND_BASE;
                break;
        }
        this.setChanged();
        this.notifyObservers();
    }

    public void setField(int y, int x, FieldEnum field) {
        this.landscape[y][x] = field;
        this.setChanged();
        this.notifyObservers();
    }

    public FieldEnum getField(int y, int x) {
        return this.landscape[y][x];
    }

    public FieldEnum[][] getLandscape() {
        return landscape;
    }

    boolean isNotEndOfField(int y, int x) {
        return (y > -1 && y < this.landscape.length)
                && (x > -1 && x < this.landscape[y].length);
    }

    List<Flag> getFlags() {
        return flags;
    }

    void setFlags(List<Flag> flags) {
        this.flags = flags;
    }
}
