package ctfmodell.container;

import java.util.ArrayList;
import java.util.List;

public class SimulationContainer {

    private List<String> openSimulations = new ArrayList<>();

    public void addSimulation(String simulation) {
        this.openSimulations.add(simulation);
    }

    public void removeSimulation(String simulation) {
        this.openSimulations.remove(simulation);
    }

    public boolean constainsSimulation(String simulation) {
        return this.openSimulations.contains(simulation);
    }

    public boolean isEmpty() {
        return this.openSimulations.isEmpty();
    }

    public int getContainerSize() {
        return openSimulations.size();
    }

}
