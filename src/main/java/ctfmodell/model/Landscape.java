package ctfmodell.model;

import ctfmodell.model.enums.Field;
import ctfmodell.model.exception.LandscapeException;
import ctfmodell.util.Coordinates;
import ctfmodell.util.GraphicSize;
import ctfmodell.util.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Landscape extends Observable {

    private PoliceOfficer policeOfficer;
    private int height;
    private int width;
    private int baseX;
    private int baseY;
    private Field[][] landscape;
    private List<Flag> flags;
    private Rectangle[][] landscapeCoordinates;

    public Landscape() {
        this.height = 10;
        this.width = 10;
        this.baseX = 9;
        this.baseY = 9;

        this.landscape = this.generateFields();
        this.flags = new ArrayList<>();
        this.landscapeCoordinates = new Rectangle[landscape.length][landscape[0].length];
        this.regeneratePixelLandscape();

        //Nur Provisorisch, da beim kompilieren sofort ersetzt
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

    private Field[][] generateFields() {
        Field[][] fields = new Field[this.height][this.width];
        for (int y = 0; y < fields.length; y++) {
            for (int x = 0; x < fields[y].length; x++) {
                if (x == this.baseX && y == this.baseY) {
                    fields[y][x] = Field.BASE;
                } else {
                    fields[y][x] = Field.EMPTY;
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
                landscapeCoordinates[y][x] = new Rectangle(posY, posY + GraphicSize.RECT_SIZE, posX, posX + GraphicSize.RECT_SIZE);
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
                this.setField(y, x, Field.FLAG);
                this.flags.add(flag);
                break;
            case POLICE_OFFICER:
                this.setField(y, x, Field.OFFICER_AND_FLAG);
                this.flags.add(flag);
                break;
            default:
                throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann keine Flagge platziert werden!", y, x));
        }
    }

    public void addUnarmedTerrorist(int y, int x) {
        if (this.landscape[y][x] == Field.EMPTY || this.landscape[y][x] == Field.ARMED_TERRORIST) {
            this.setField(y, x, Field.UNARMED_TERRORIST);

        } else {
            throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann kein Terrorist platziert werden!", y, x));
        }
    }

    public void addArmedTerrorist(int y, int x) {
        if (this.landscape[y][x] == Field.EMPTY || this.landscape[y][x] == Field.UNARMED_TERRORIST) {
            this.setField(y, x, Field.ARMED_TERRORIST);
        } else {
            throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann kein Terrorist platziert werden!", y, x));
        }
    }

    public void updatePoliceOfficer(PoliceOfficer policeOfficer) {
        PoliceOfficer oldOfficer = this.unsetPoliceOfficer();
        if (oldOfficer != null) {
            policeOfficer.setxPos(oldOfficer.getxPos());
            policeOfficer.setyPos(oldOfficer.getyPos());
            policeOfficer.setNumberOfFlags(oldOfficer.getNumberOfFlags());
            policeOfficer.setDirection(oldOfficer.getDirection());
        }
        this.setPoliceOfficer(policeOfficer);
        this.policeOfficer.setLandscape(this);
    }

    public void clearOriginPolice(Integer y, Integer x) {
        Field field = this.landscape[y][x];
        switch (field) {
            case OFFICER_AND_BASE:
//                this.setField(y, x, Field.BASE);
                this.landscape[y][x] = Field.BASE;
                break;
            case OFFICER_AND_FLAG:
//                this.setField(y, x, Field.FLAG);
                this.landscape[y][x] = Field.FLAG;
                break;
            case POLICE_OFFICER:
//                this.setField(y, x, Field.EMPTY); Für Simulation nicht geeignet
                this.landscape[y][x] = Field.EMPTY;
        }
    }

    public PoliceOfficer getPoliceOfficer() {
        return policeOfficer;
    }

    public void setDestinationPolice(Integer y, Integer x) {
        Field field = this.landscape[y][x];
        this.getPoliceOfficer().setyPos(y);
        this.getPoliceOfficer().setxPos(x);
        switch (field) {
            case EMPTY:
                this.setField(y, x, Field.POLICE_OFFICER);
                break;
            case FLAG:
                this.setField(y, x, Field.OFFICER_AND_FLAG);
                break;
            case BASE:
                this.setField(y, x, Field.OFFICER_AND_BASE);
        }
    }

    public void resize(int width, int height) {
        if (this.landscape == null)
            throw new LandscapeException("Du kannst nicht die Größe einer nicht existierenden Landschaft verändern!");
        if (width < 2 || height < 2)
            throw new LandscapeException("Eine neue Landschaft muss mindestens 2x2 groß sein!");

        this.width = width;
        this.height = height;
        Field[][] resizedLandscape = new Field[height][width];
        landscapeCoordinates = new Rectangle[height][width];

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
                    resizedLandscape[y][x] = Field.EMPTY;
                }
            }
        }

        //Falls das vorherige Feld einen Akteur und oder eine Base hatte aber nicht zum neuen übernommen wurde,
        //Füge Officer hinzu
        if (hasPoliceOfficerOnLandscape(this.landscape) && !hasPoliceOfficerOnLandscape(resizedLandscape)) {
            Field fieldToCheck = getFieldEnum(0, 0);
            if (fieldToCheck == Field.BASE) resizedLandscape[0][0] = Field.OFFICER_AND_BASE;
            else if (fieldToCheck == Field.FLAG) resizedLandscape[0][0] = Field.OFFICER_AND_FLAG;
            else resizedLandscape[0][0] = Field.POLICE_OFFICER;
        }

        if (hasBaseOnLandscape(this.landscape) && !hasBaseOnLandscape(resizedLandscape)) {
            Field fieldToCheck = getFieldEnum(resizedLandscape.length, resizedLandscape[0].length);
            if (fieldToCheck == Field.POLICE_OFFICER) resizedLandscape[0][0] = Field.OFFICER_AND_BASE;
            else resizedLandscape[height - 1][width - 1] = Field.BASE;
        }

        this.landscape = resizedLandscape;
        this.regeneratePixelLandscape();
        this.setChanged();
        this.notifyObservers();
    }

    private boolean hasPoliceOfficerOnLandscape(Field[][] landscape) {
        for (Field[] fields : landscape) {
            for (Field field : fields) {
                if (field == Field.OFFICER_AND_FLAG ||
                        field == Field.OFFICER_AND_BASE ||
                        field == Field.POLICE_OFFICER) {
                    return true;
                }
            }
        }
        return false;
    }

    private Field getFieldEnum(int y, int x) {
        return this.landscape[y][x];
    }

    private boolean hasBaseOnLandscape(Field[][] landscape) {
        for (Field[] fields : landscape) {
            for (Field field : fields) {
                if (field == Field.OFFICER_AND_BASE ||
                        field == Field.BASE) {
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

    public void clearOriginBase(Integer y, Integer x) {
        Field field = this.landscape[y][x];
        switch (field) {
            case OFFICER_AND_BASE:
//                this.setField(y, x, Field.POLICE_OFFICER);
                this.landscape[y][x] = Field.POLICE_OFFICER;
                break;
            case BASE:
//                this.setField(y, x, Field.EMPTY);
                this.landscape[y][x] = Field.EMPTY;
                break;
        }
    }

    public void setDestinationBase(Integer y, Integer x) {
        Field field = this.landscape[y][x];
        this.getPoliceOfficer().setyPos(y);
        this.getPoliceOfficer().setxPos(x);
        switch (field) {
            case EMPTY:
                this.setField(y, x, Field.BASE);
                break;
            case POLICE_OFFICER:
                this.setField(y, x, Field.OFFICER_AND_BASE);
                break;
        }
    }

    private PoliceOfficer unsetPoliceOfficer() {
        int y = this.policeOfficer.getyPos();
        int x = this.policeOfficer.getxPos();

        Field field = this.landscape[y][x];

        switch (field) {
            case OFFICER_AND_BASE:
                this.setField(y, x, Field.BASE);
            case OFFICER_AND_FLAG:
                this.setField(y, x, Field.FLAG);
            case POLICE_OFFICER:
//                this.setField(y, x, Field.EMPTY);
                this.landscape[y][x] = Field.EMPTY;
        }

        return this.getPoliceOfficer();
    }

    private void setPoliceOfficer(PoliceOfficer policeOfficer) {
        this.policeOfficer = policeOfficer;
        int x = policeOfficer.getxPos();
        int y = policeOfficer.getyPos();
        switch (this.landscape[y][x]) {
            case EMPTY:
            case POLICE_OFFICER:
                this.setField(y, x, Field.POLICE_OFFICER);
                break;
            case FLAG:
            case OFFICER_AND_FLAG:
                this.setField(y, x, Field.OFFICER_AND_FLAG);
                break;
            case BASE:
            case OFFICER_AND_BASE:
                this.setField(y, x, Field.OFFICER_AND_BASE);
                break;
            default:
                throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) kann kein Officer platziert werden!", y, x));

        }
    }

    public void setField(int y, int x, Field field) {
        this.landscape[y][x] = field;
        this.setChanged();
        this.notifyObservers();
    }

    public Field getField(int y, int x) {
        return this.landscape[y][x];
    }

    public Field[][] getLandscape() {
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
