package ctfmodell.util;

/**
 * Klickbare Umsetzung eines Feldes für den Canvas
 *
 * @author Nick Garbusa
 */
public class Rectangle {

    private int minY;
    private int maxY;
    private int minX;
    private int maxX;

    public Rectangle(int minY, int maxY, int minX, int maxX) {
        this.minY = minY;
        this.maxY = maxY;
        this.minX = minX;
        this.maxX = maxX;
    }

    public boolean isValidPixel(int y, int x) {
        return (y >= minY && y <= maxY) && (x >= minX && x <= maxX);
    }

}
