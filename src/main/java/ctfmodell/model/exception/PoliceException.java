package ctfmodell.model.exception;

/**
 * Wenn eine Interaktion durch den Police Officer fehl schlägt
 * z.B. attackieren eines bewaffneten Terroristen
 *
 * @author Nick Garbusa
 */
public class PoliceException extends RuntimeException {

    public PoliceException(String message) {
        super(message);
    }

}
