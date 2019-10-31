package ctfmodell.model;

import ctfmodell.model.enums.DirectionEnum;
import ctfmodell.model.enums.FieldEnum;
import ctfmodell.model.exception.*;

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

    /* **************************************** ACTIONS ******************************************* */

    public void forward() {
        int x = this.xPos;
        int y = this.yPos;
        switch (this.direction) {
            case NORTH:
                this.forwardToDirection(x, y - 1);
                break;
            case SOUTH:
                this.forwardToDirection(x, y + 1);
                break;
            case WEST:
                this.forwardToDirection(x - 1, y);
                break;
            case EAST:
                this.forwardToDirection(x + 1, y);
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
            throw new PoliceException("Du kannst nur unbewaffnete Terroristen attackieren!");
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
        if (actualField != FieldEnum.OFFICER_AND_FLAG) {
            throw new FlagException("Auf diesem Feld gibt es keine Flagge zum aufheben!");
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
        if (actualField != FieldEnum.OFFICER_AND_BASE) {
            throw new BaseException("Du musst dich in der Base befinden, um deine Flaggen abzulegen!");
        } else if (!this.hasFlags()) {
            throw new FlagException("Du hast keine Flaggen im Besitz!");
        } else if (!this.hasAllFlags()) {
            throw new FlagException("Du hast nicht genügend Flaggen gesammelt!");
        } else {
            this.numberOfFlags = 0;
            this.hasWon = true;
        }
    }

    /* **************************************** HELPER ******************************************* */

    private boolean hasFlags() {
        return this.numberOfFlags > 0;
    }

    private boolean hasAllFlags() {
        return this.landscape.getFlags().size() == 0;
    }

    public boolean hasWon() {
        return hasWon;
    }

    private boolean isNotEndOfField(int y, int x) {
        return (y > -1 && y < this.landscape.getLandscape().length)
                && (x > -1 && x < this.landscape.getLandscape()[y].length);
    }


    private FieldEnum getForwardField() {
        int y = this.yPos;
        int x = this.xPos;
        switch (this.direction) {
            case NORTH:
                if (this.isNotEndOfField(y - 1, x)) {
                    return this.landscape.getLandscape()[y - 1][x];
                } else {
                    return FieldEnum.OUT_OF_FIELD;
                }
            case SOUTH:
                if (this.isNotEndOfField(y + 1, x)) {
                    return this.landscape.getLandscape()[y + 1][x];
                } else {
                    return FieldEnum.OUT_OF_FIELD;
                }
            case WEST:
                if (this.isNotEndOfField(y, x - 1)) {
                    return this.landscape.getLandscape()[y][x - 1];
                } else {
                    return FieldEnum.OUT_OF_FIELD;
                }
            case EAST:
                if (this.isNotEndOfField(y, x + 1)) {
                    return this.landscape.getLandscape()[y][x + 1];
                } else {
                    return FieldEnum.OUT_OF_FIELD;
                }
            default:
                break;
        }

        return null;
    }

    private void setForwardField(FieldEnum field) {
        int y = this.yPos;
        int x = this.xPos;
        switch (this.direction) {
            case NORTH:
                if (this.isNotEndOfField(y - 1, x)) {
                    this.landscape.getLandscape()[y - 1][x] = field;
                } else {
                    System.out.println("Koordinaten befinden sich außerhalb des Feldes!");
                    throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) befindet sich kein exestierendes Feld!", y - 1, x));
                }
            case SOUTH:
                if (this.isNotEndOfField(y + 1, x)) {
                    this.landscape.getLandscape()[y + 1][x] = field;
                } else {
                    throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) befindet sich kein exestierendes Feld!", y + 1, x));
                }
            case WEST:
                if (this.isNotEndOfField(y, x - 1)) {
                    this.landscape.getLandscape()[y][x - 1] = field;
                } else {
                    throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) befindet sich kein exestierendes Feld!", y, x - 1));
                }
            case EAST:
                if (this.isNotEndOfField(y, x + 1)) {
                    this.landscape.getLandscape()[y][x + 1] = field;
                } else {
                    throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) befindet sich kein exestierendes Feld!", y, x + 1));
                }
            default:
                break;
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

    private void forwardToDirection(int newX, int newY) {
        FieldEnum forwardField = getForwardField();
        if (forwardField != FieldEnum.UNARMED_TERRORIST && forwardField != FieldEnum.ARMED_TERRORIST &&
                forwardField != FieldEnum.OUT_OF_FIELD) {
            setOldPosition();
            this.xPos = newX;
            this.yPos = newY;
            setNewPosition();
        } else {
            this.throwMoveError(forwardField);
        }
    }

    /* **************************************** STANDARD GETTER & SETTER ******************************************* */

    private FieldEnum getActualField() {
        return this.landscape.getLandscape()[this.yPos][this.xPos];
    }

    private void setActualField(FieldEnum field) {
        this.landscape.getLandscape()[this.yPos][this.xPos] = field;
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

    public Landscape getLandscape() {
        return landscape;
    }

    public void setLandscape(Landscape landscape) {
        this.landscape = landscape;
    }

    /* **************************************** ERROR HANDLING ******************************************* */

    private void throwMoveError(FieldEnum field) {
        if (field == FieldEnum.UNARMED_TERRORIST || field == FieldEnum.ARMED_TERRORIST) {
            throw new MoveException("Du kannst dich nicht fortbewegen, da dir etwas im Weg steht!");
        } else if (field == FieldEnum.OUT_OF_FIELD) {
            throw new MoveException("Du kannst dich nicht ins Nichts bewegen!");
        }
    }
}
