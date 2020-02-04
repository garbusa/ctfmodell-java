package ctfmodell.model.exception;

/**
 * Eine Exception die auftritt, wenn etwas mit der Flagge schief läuft
 * z.B. illegales Setzen
 *
 * @author Nick Garbusa
 */
public class FlagException extends RuntimeException {

    public FlagException(String message) {
        super(message);
    }

}
