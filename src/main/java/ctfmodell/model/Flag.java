package ctfmodell.model;

public class Flag {

    private boolean pickedUp;
    private int xPos;
    private int yPos;

    private Flag() {
    }

    public Flag(boolean pickedUp, int xPos, int yPos) {
        this.pickedUp = pickedUp;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }
}
