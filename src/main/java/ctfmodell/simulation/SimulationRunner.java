package ctfmodell.simulation;

/*
  Ich war zum Thread-Unterricht leider nicht da, musste deshalb recherchieren
  https://stackoverflow.com/questions/16758346/how-pause-and-then-resume-a-thread
*/

import ctfmodell.controller.Controller;
import ctfmodell.model.Landscape;
import ctfmodell.model.PoliceOfficer;
import ctfmodell.provider.SoundProvider;
import ctfmodell.simulation.exception.SimulationException;
import javafx.application.Platform;

import java.util.Observable;
import java.util.Observer;

/**
 * Das Runnable, wo die Simulation läuft
 * Starten, Pausieren und Stoppen der Simulation ist hier möglich (wird durch die
 * Controls im Controller veranlasst)
 *
 * @author Nick Garbusa
 */
public class SimulationRunner implements Runnable, Observer {

    private final Object pauseLock = new Object();
    public volatile boolean running = false;
    public volatile boolean paused = false;
    private double speed;
    private Landscape landscape;
    private Controller controller;

    public SimulationRunner(double speed, Landscape landscape, Controller controller) {
        this.controller = controller;
        this.speed = speed;
        this.landscape = landscape;
        landscape.addObserver(this);
        landscape.getPoliceOfficer().addObserver(this);
    }

    public void setLandscape(Landscape landscape) {
        this.landscape = landscape;
    }

    @Override
    public void run() {
        this.running = true;
        try {
            landscape.getPoliceOfficer().main();
            this.running = false;
            this.paused = false;
        } catch (Exception e) {
            System.err.println("[Simulation] " + e.getMessage());
            SoundProvider.beep();
        } finally {
            this.controller.setSimulationControls(false, true, true);
        }
        this.running = false;
        this.paused = false;
    }


    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof PoliceOfficer))
            this.landscape = (Landscape) o;

        try {
            if (!Platform.isFxApplicationThread()) {
                if (!running) throw new SimulationException("Simulation wurde beendet");
                synchronized (pauseLock) {
                    if (paused) {
                        try {
                            synchronized (pauseLock) {
                                pauseLock.wait();
                            }
                        } catch (InterruptedException ex) {
                            throw new SimulationException("Simulation wurde beendet");
                        }
                    }
                    if (!running) throw new SimulationException("Simulation wurde beendet");
                }
                Thread.sleep((long) this.speed);
            }
        } catch (InterruptedException e) {
            System.err.println("Simulations-Thread wurde gestoppt");
        }
    }

    public void stop() {
        running = false;
        synchronized (pauseLock) {
            this.pauseLock.notify();
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}

