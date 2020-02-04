package ctfmodell.model.exception;

/**
 * Eine Exception die auftritt, wenn etwas beim fortbewegen schief läuft
 * z.B. gegen eine Wand/Terrorist laufen
 *
 * @author Nick Garbusa
 */
public class MoveException extends RuntimeException {

    public MoveException(String message) {
        super(message);
    }

}
