package ctfmodell.util;

public class PixelRectangle {

    public PixelRectangle(int minY, int maxY, int minX, int maxX) {
        this.minY = minY;
        this.maxY = maxY;
        this.minX = minX;
        this.maxX = maxX;
    }

    private int minY;
    private int maxY;
    private int minX;
    private int maxX;

    public boolean isValidPixel(int y, int x) {
        return (y >= minY && y <= maxY) && (x >= minX && x <= maxX);
    }

}
