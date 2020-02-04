package ctfmodell.util;

/**
 * Objekt, welches die x,y Koordinaten eines Feldes speichert
 */
public class Coordinates {
    private Integer y;
    private Integer x;

    public Coordinates(Integer y, Integer x) {
        this.y = y;
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getX() {
        return x;
    }
}
