package ctfmodell.model;

import ctfmodell.model.enums.DirectionEnum;
import ctfmodell.model.enums.FieldEnum;

import java.util.Iterator;
import java.util.List;

public class PoliceOfficer {

    private int yPos;
    private int xPos;
    private int numberOfFlags;
    private DirectionEnum direction;
    private Landscape landscape;
    private boolean hasWon;

    private PoliceOfficer() {
    }

    public PoliceOfficer(int yPos, int xPos, DirectionEnum direction) {
        this.yPos = yPos;
        this.xPos = xPos;
        this.numberOfFlags = 0;
        this.direction = direction;
        this.hasWon = false;
    }

    public FieldEnum getForwardField() {
        int y = this.yPos;
        int x = this.xPos;
        switch (this.direction) {
            case NORTH:
                return this.landscape.getLandscape()[y - 1][x];
            case SOUTH:
                return this.landscape.getLandscape()[y + 1][x];
            case WEST:
                return this.landscape.getLandscape()[y][x - 1];
            case EAST:
                return this.landscape.getLandscape()[y][x + 1];
            default:
                break;
        }

        return null;
    }

    public void setForwardField(FieldEnum field) {
        int y = this.yPos;
        int x = this.xPos;
        switch (this.direction) {
            case NORTH:
                this.landscape.getLandscape()[y - 1][x] = field;
            case SOUTH:
                this.landscape.getLandscape()[y + 1][x] = field;
            case WEST:
                this.landscape.getLandscape()[y][x - 1] = field;
            case EAST:
                this.landscape.getLandscape()[y][x + 1] = field;
            default:
                break;
        }

    }

    public FieldEnum getActualField() {
        return this.landscape.getLandscape()[this.yPos][this.xPos];
    }

    public void setActualField(FieldEnum field) {
        this.landscape.getLandscape()[this.yPos][this.xPos] = field;
    }

    public void forward() {

        FieldEnum forwardField;
        switch (this.direction) {
            case NORTH:
                forwardField = getForwardField();
                if (forwardField != FieldEnum.UNARMED_TERRORIST && forwardField != FieldEnum.ARMED_TERRORIST) {
                    setOldPosition();
                    this.yPos--;
                    setNewPosition();
                } else {
                    System.out.println("Etwas versperrt dir den Weg!");
                }
                break;
            case SOUTH:
                forwardField = getForwardField();
                if (forwardField != FieldEnum.UNARMED_TERRORIST && forwardField != FieldEnum.ARMED_TERRORIST) {
                    setOldPosition();
                    this.yPos++;
                    setNewPosition();
                } else {
                    System.out.println("Etwas versperrt dir den Weg!");
                }
                break;
            case WEST:
                forwardField = getForwardField();
                if (forwardField != FieldEnum.UNARMED_TERRORIST && forwardField != FieldEnum.ARMED_TERRORIST) {
                    setOldPosition();
                    this.xPos--;
                    setNewPosition();
                } else {
                    System.out.println("Etwas versperrt dir den Weg!");
                }
                break;
            case EAST:
                forwardField = getForwardField();
                if (forwardField != FieldEnum.UNARMED_TERRORIST && forwardField != FieldEnum.ARMED_TERRORIST) {
                    setOldPosition();
                    this.xPos++;
                    setNewPosition();
                } else {
                    System.out.println("Etwas versperrt dir den Weg!");
                }
                break;
            default:
                break;
        }
    }

    public void attack() {
        FieldEnum forwardField = getForwardField();
        if (forwardField == FieldEnum.UNARMED_TERRORIST) {
            this.setForwardField(FieldEnum.EMPTY);
        } else {
            System.out.println("Attackieren ist nicht möglich");
        }
    }

    private void setNewPosition() {
        FieldEnum actualField = getActualField();
        if (actualField == FieldEnum.BASE) {
            this.setActualField(FieldEnum.OFFICER_AND_BASE);
        } else if (actualField == FieldEnum.FLAG) {
            this.setActualField(FieldEnum.OFFICER_AND_FLAG);
        } else {
            this.setActualField(FieldEnum.POLICE_OFFICER);
        }
    }

    private void setOldPosition() {
        FieldEnum actualField = this.getActualField();
        if (actualField == FieldEnum.OFFICER_AND_BASE) {
            this.setActualField(FieldEnum.BASE);
        } else if (actualField == FieldEnum.OFFICER_AND_FLAG) {
            this.setActualField(FieldEnum.FLAG);
        } else {
            this.setActualField(FieldEnum.EMPTY);
        }
    }

    public void turnLeft() {
        switch (this.direction) {
            case NORTH:
                this.direction = DirectionEnum.WEST;
                break;
            case SOUTH:
                this.direction = DirectionEnum.EAST;
                break;
            case WEST:
                this.direction = DirectionEnum.SOUTH;
                break;
            case EAST:
                this.direction = DirectionEnum.NORTH;
                break;
            default:
                break;
        }
    }

    public void pick() {
        //Check if there is a flag
        FieldEnum actualField = getActualField();
        if(actualField != FieldEnum.OFFICER_AND_FLAG) {
            System.out.println("Es gibt keine Flagge zum aufheben");
        } else {
            //Update Field
            setActualField(FieldEnum.POLICE_OFFICER);

            //Remove Flag from List
            List<Flag> flags = this.landscape.getFlags();
            Iterator<Flag> iter = flags.iterator();

            while (iter.hasNext()) {
                Flag flag = iter.next();

                if (flag.getxPos() == this.getxPos() && flag.getyPos() == this.getyPos())
                    iter.remove();
            }

            this.landscape.setFlags(flags);

            //Add flag to officer
            this.numberOfFlags++;
        }
    }

    public void drop() {
        FieldEnum actualField = getActualField();
        if(actualField != FieldEnum.OFFICER_AND_BASE) {
            System.out.println("Du befindest dich nicht in deiner Base!");
        } else if(!this.hasFlags()) {
            System.out.println("Du hast keine Flaggen!");
        } else if(!this.hasAllFlags()) {
            System.out.println("Du hast nicht alle Flaggen gesammelt!");
        } else {
            this.numberOfFlags = 0;
            this.hasWon = true;
        }
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getNumberOfFlags() {
        return numberOfFlags;
    }

    public void setNumberOfFlags(int numberOfFlags) {
        this.numberOfFlags = numberOfFlags;
    }

    public DirectionEnum getDirection() {
        return direction;
    }

    public void setDirection(DirectionEnum direction) {
        this.direction = direction;
    }

    private boolean hasFlags() {
        return this.numberOfFlags > 0;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public void setLandscape(Landscape landscape) {
        this.landscape = landscape;
    }

    public boolean hasAllFlags() {
        return this.landscape.getFlags().size() == 0;
    }

    public boolean hasWon() {
        return hasWon;
    }
}
