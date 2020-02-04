package ctfmodell.model.exception;

/**
 * Eine Exception die auftritt, wenn etwas mit der Base schief läuft
 * z.B. illegales Setzen
 *
 * @author Nick Garbusa
 */
public class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }

}
