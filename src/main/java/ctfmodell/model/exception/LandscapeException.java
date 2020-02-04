package ctfmodell.model.exception;

/**
 * Eine Exception die auftritt, wenn etwas mit der Landschaft schief läuft
 * z.B. Resizen auf 1x1
 *
 * @author Nick Garbusa
 */
public class LandscapeException extends RuntimeException {

    public LandscapeException(String message) {
        super(message);
    }

}
