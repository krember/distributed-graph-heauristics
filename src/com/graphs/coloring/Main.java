package com.graphs.coloring;

import com.graphs.coloring.simulators.AcyclicTournamentSimulator;
import com.graphs.common.simulators.Simulator;
import com.graphs.forest_decomposition.simulators.CompleteBipartiteGraphSimulator;
import com.graphs.forest_decomposition.simulators.CompleteGraphSimulator;
import com.graphs.forest_decomposition.simulators.PlanarGraphSimulator;
import com.graphs.forest_decomposition.simulators.RandomSimulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // 60 - 300 / x60  _Normal
        // 0.2  / 0.5 / 0.9

        // 40 - 80  / x 10

        // 1500 +     _Large
        // 0.2  / 0.5 / 0.9


        /** General run { [60, 300]  --  +60 --  [0.2, 0.5, 0.9]  } */
//        List<Double> probablilities = List.of(0.2, 0.5, 0.9);
//
//        simulate(probablilities, 60, 300, 60);

        /** Specific run { [15, 165] --  +25 --  [0.9]  } */
//        List<Double> probablilities = List.of(0.9);
//
//        simulate(probablilities, 15, 165, 25);

        /** Specific run { [60, 300] --  +60 --  [0.1, 0.15, 0.2, 0.25, 0.3]  } */
        List<Double> probablilities = List.of(0.1, 0.15, 0.2, 0.25, 0.3);

        simulate(probablilities, 60, 300, 60);

    }

    private static void simulate(List<Double> probs, int verticesLowerBound, int verticesUpperBound, int step) throws InterruptedException {
        for (Double probability: probs) {
            int numberOfVertices = verticesLowerBound;
            while (numberOfVertices <= verticesUpperBound) {

                int avgSuccessIterations = 0;
                int successCases = 0;
                int testCount = 100;

                for(int i = 0; i < testCount; ++i) {
                    AcyclicTournamentSimulator simulator =
                            new AcyclicTournamentSimulator(numberOfVertices, numberOfVertices * 100, probability, probability,  false);

                    simulator.simulate();

                    if(simulator.isSuccess()) {
                        avgSuccessIterations += simulator.getIterations();
                        ++successCases;
                    }
                }

                System.out.println("Number of vertices: " + numberOfVertices);
                System.out.println("P = " + probability);
                System.out.println("Success cases: " + successCases * 100 /testCount + "%");
                System.out.println("AVG iterations: " + avgSuccessIterations / testCount);
                System.out.println("Failure cases: " + (100 - successCases * 100/testCount) + "%");
                System.out.println();

                numberOfVertices += step;
            }
        }
    }
}
