package ctfmodell.model;

/**
 * Flaggen Objekt
 */
public class Flag {

    private int xPos;
    private int yPos;

    private Flag() {
    }

    public Flag(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }
}
