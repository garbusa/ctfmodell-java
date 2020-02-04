package ctfmodell.simulation.exception;

/**
 * Eine Exception die auftritt, wenn z.B. eine Simulation durch die Controls
 * oder aus unerwarteten Gründen abgebrochen wird
 *
 * @author Nick Garbusa
 */
public class SimulationException extends RuntimeException {

    public SimulationException(String message) {
        super(message);
    }

}
