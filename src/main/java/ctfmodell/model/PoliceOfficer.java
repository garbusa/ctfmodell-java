package ctfmodell.model;

import ctfmodell.model.enums.Direction;
import ctfmodell.model.enums.Field;
import ctfmodell.model.exception.*;

import java.util.List;
import java.util.Observable;

public class PoliceOfficer extends Observable {

    protected int yPos;
    protected int xPos;
    protected Direction direction;
    private int numberOfFlags;
    private Landscape landscape;
    private boolean hasWon;

    public PoliceOfficer() {
        this(0, 0, Direction.EAST);
    }

    private PoliceOfficer(int yPos, int xPos, Direction direction) {
        this.yPos = yPos;
        this.xPos = xPos;
        this.numberOfFlags = 0;
        this.direction = direction;
        this.hasWon = false;
    }

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

    protected void forwardToDirection(int newX, int newY) {
        Field forwardField = this.getForwardField();
        if (forwardField != Field.UNARMED_TERRORIST && forwardField != Field.ARMED_TERRORIST &&
                forwardField != Field.OUT_OF_FIELD) {
            this.setOldPosition();
            this.xPos = newX;
            this.yPos = newY;
            this.setNewPosition();
        } else {
            this.throwMoveError(forwardField);
        }
    }

    private Field getForwardField() {
        int y = this.yPos;
        int x = this.xPos;
        switch (this.direction) {
            case NORTH:
                return this.checkEndAndGetForward(y - 1, x);
            case SOUTH:
                return this.checkEndAndGetForward(y + 1, x);
            case WEST:
                return this.checkEndAndGetForward(y, x - 1);
            case EAST:
                return this.checkEndAndGetForward(y, x + 1);
            default:
                break;
        }

        return null;
    }

    private void setOldPosition() {
        Field actualField = this.getActualField();
        if (actualField == Field.OFFICER_AND_BASE) {
            this.setActualField(Field.BASE);
        } else if (actualField == Field.OFFICER_AND_FLAG) {
            this.setActualField(Field.FLAG);
        } else {
            this.setActualField(Field.EMPTY);
        }
    }

    private void setNewPosition() {
        Field actualField = this.getActualField();
        if (actualField == Field.BASE) {
            this.setActualField(Field.OFFICER_AND_BASE);
        } else if (actualField == Field.FLAG) {
            this.setActualField(Field.OFFICER_AND_FLAG);
        } else {
            this.setActualField(Field.POLICE_OFFICER);
        }
    }

    private void throwMoveError(Field field) {
        if (field == Field.UNARMED_TERRORIST || field == Field.ARMED_TERRORIST) {
            throw new MoveException("Du kannst dich nicht fortbewegen, da dir etwas im Weg steht!");
        } else if (field == Field.OUT_OF_FIELD) {
            throw new MoveException("Du kannst dich nicht ins Nichts bewegen!");
        }
    }

    private Field checkEndAndGetForward(int y, int x) {
        if (this.isNotEndOfField(y, x)) {
            return this.landscape.getField(y, x);
        } else {
            return Field.OUT_OF_FIELD;
        }
    }

    private Field getActualField() {
        return this.landscape.getLandscape()[this.yPos][this.xPos];
    }

    private boolean isNotEndOfField(int y, int x) {
        return this.landscape.isNotEndOfField(y, x);
    }

    private void setActualField(Field field) {
        this.landscape.setField(this.yPos, this.xPos, field);
    }

    public void attack() {
        Field forwardField = this.getForwardField();
        if (forwardField == Field.UNARMED_TERRORIST) {
            this.clearForwardField();
        } else {
            throw new PoliceException("Du kannst nur unbewaffnete Terroristen attackieren!");
        }
    }

    private void clearForwardField() {
        int y = this.yPos;
        int x = this.xPos;
        switch (this.direction) {
            case NORTH:
                this.checkEndAndClearForward(y - 1, x);
                break;
            case SOUTH:
                this.checkEndAndClearForward(y + 1, x);
                break;
            case WEST:
                this.checkEndAndClearForward(y, x - 1);
                break;
            case EAST:
                this.checkEndAndClearForward(y, x + 1);
                break;
            default:
                break;
        }
    }

    private void checkEndAndClearForward(int y, int x) {
        if (this.isNotEndOfField(y, x)) {
            this.landscape.setField(y, x, Field.EMPTY);
        } else {
            System.out.println("Koordinaten befinden sich außerhalb des Feldes!");
            throw new LandscapeException(String.format("Auf den Koordinaten (%d,%d) befindet sich kein exestierendes Feld!", y, x));
        }
    }

    public void drop() {
        Field actualField = this.getActualField();
        if (actualField != Field.OFFICER_AND_BASE) {
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

    private boolean hasFlags() {
        return this.numberOfFlags > 0;
    }

    private boolean hasAllFlags() {
        return this.landscape.getFlags().size() == 0;
    }

    public boolean hasWon() {
        return this.hasWon;
    }

    public boolean isFree() {
        Field field = this.getForwardField();
        return field == Field.EMPTY || field == Field.FLAG || field == Field.BASE;
    }

    public boolean isAttackable() {
        return this.getForwardField() == Field.UNARMED_TERRORIST;
    }

    public boolean isBase() {
        return this.landscape.getLandscape()[this.yPos][this.xPos] == Field.OFFICER_AND_BASE;
    }

    public boolean isFlag() {
        return this.landscape.getLandscape()[this.yPos][this.xPos] == Field.OFFICER_AND_FLAG;
    }

    public void turnLeft() {
        switch (this.direction) {
            case NORTH:
                this.direction = Direction.WEST;
                break;
            case WEST:
                this.direction = Direction.SOUTH;
                break;
            case SOUTH:
                this.direction = Direction.EAST;
                break;
            case EAST:
                this.direction = Direction.NORTH;
                break;
            default:
                break;
        }
        this.setChanged();
        this.notifyObservers(this);
    }

    public void pick() {
        //Check if there is a flag
        Field actualField = this.getActualField();
        if (actualField != Field.OFFICER_AND_FLAG) {
            throw new FlagException("Auf diesem Feld gibt es keine Flagge zum aufheben!");
        } else {
            //Update Field
            this.setActualField(Field.POLICE_OFFICER);

            //Remove Flag from List
            List<Flag> flags = this.landscape.getFlags();

            flags.removeIf(flag -> flag.getxPos() == this.getxPos() && flag.getyPos() == this.getyPos());

            this.landscape.setFlags(flags);

            //Add flag to officer
            this.numberOfFlags++;
        }
    }

    int getxPos() {
        return xPos;
    }

    int getyPos() {
        return yPos;
    }

    void setyPos(int yPos) {
        this.yPos = yPos;
    }

    void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getNumberOfFlags() {
        return numberOfFlags;
    }

    public void setNumberOfFlags(int numberOfFlags) {
        this.numberOfFlags = numberOfFlags;
    }

    void setLandscape(Landscape landscape) {
        this.landscape = landscape;
    }
}
