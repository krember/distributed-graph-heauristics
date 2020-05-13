package com.graphs;

import com.graphs.simulators.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<Simulator> simulators = new ArrayList<>();

        simulators.add(new RandomSimulator(50, 150, 0.001));
        simulators.add(new CompleteGraphSimulator(150, 0.001));
        simulators.add(new CompleteBipartiteGraphSimulator(35, 43, 0.001));
        simulators.add(new PlanarGraphSimulator(60, 0.001));

        for (Simulator simulator : simulators) {
            System.out.println("\n\n~~~~~ Simulation Start ~~~~~\n\n");
            simulator.simulate();
            Thread.sleep(5000);
        }
    }

}
