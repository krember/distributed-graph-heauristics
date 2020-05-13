package com.graphs.simulators;

import com.graphs.Vertex;

import java.util.ArrayList;
import java.util.List;

public class CompleteBipartiteGraphSimulator implements Simulator {

    private List<Vertex> vertices1 = new ArrayList<>();
    private List<Vertex> vertices2 = new ArrayList<>();
    private int arboricity;
    private int numberOfVerticesPart1;
    private int numberOfVerticesPart2;
    private double eps;

    public CompleteBipartiteGraphSimulator(int numberOfVerticesPart1, int numberOfVerticesPart2 , double eps) {
        this.numberOfVerticesPart1 = numberOfVerticesPart1;
        this.numberOfVerticesPart2 = numberOfVerticesPart2;
        this.eps = eps;

        for (int i = 1; i <= numberOfVerticesPart1; ++i) {
            vertices1.add(new Vertex(i));
        }

        for (int i = 1; i <= numberOfVerticesPart2; ++i) {
            vertices2.add(new Vertex(i + numberOfVerticesPart1));
        }

        for (int i = 0; i < numberOfVerticesPart1; ++i) {
            for (int j = 0; j < numberOfVerticesPart2; ++j) {
                vertices1.get(i).addNeighbor(vertices2.get(j));
                vertices2.get(j).addNeighbor(vertices1.get(i));
            }
        }
    }

    private void init() {
        int numberOfEdges = numberOfVerticesPart1 * numberOfVerticesPart2;
        arboricity = (int) Math.ceil(numberOfVerticesPart1 * numberOfVerticesPart2 / (numberOfVerticesPart1 + numberOfVerticesPart2 - 1));

        int numberOfVertices = numberOfVerticesPart1 + numberOfVerticesPart2;

        Vertex.numberOfEdgesYetToBeLabelled.set(numberOfEdges);
        Vertex.numberOfVerticesYetActive.set(numberOfVertices);
        Vertex.numberOfForests.set(0);

        for(Vertex vertex : vertices1) {
            vertex.init(eps, arboricity, numberOfVertices);
        }

        for(Vertex vertex : vertices2) {
            vertex.init(eps, arboricity, numberOfVertices);
        }
    }

    @Override
    public void simulate() throws InterruptedException {
        init();

        List<Thread> threads = new ArrayList<>();
        for (Vertex v : vertices1) {
            Thread thread = new Thread(v);
            threads.add(thread);
            thread.start();
        }

        for (Vertex v : vertices2) {
            Thread thread = new Thread(v);
            threads.add(thread);
            thread.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("\n\n\n");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Arboricity                  : " + arboricity);
        System.out.println("Number of decomposed forests: " + Vertex.numberOfForests.get());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
