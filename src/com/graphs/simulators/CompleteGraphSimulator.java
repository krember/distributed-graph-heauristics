package com.graphs.simulators;

import com.graphs.Vertex;

import java.util.ArrayList;
import java.util.List;

public class CompleteGraphSimulator implements Simulator {
    private List<Vertex> vertices = new ArrayList<>();
    private int arboricity;
    private int numberOfVertices;
    private double eps;

    public CompleteGraphSimulator(int numberOfVertices, double eps) {
        for (int i = 1; i <= numberOfVertices; ++i) {
            vertices.add(new Vertex(i));
        }

        for (int i = 0; i < numberOfVertices; ++i) {
            for (int j = i + 1; j < numberOfVertices; ++j) {
                vertices.get(i).addNeighbor(vertices.get(j));
                vertices.get(j).addNeighbor(vertices.get(i));
            }
        }

        this.numberOfVertices = numberOfVertices;
        this.eps = eps;
    }

    private void init() {
        int numberOfEdges = numberOfVertices * (numberOfVertices - 1) / 2;
        arboricity = (int) Math.ceil(numberOfVertices / 2);

        Vertex.numberOfEdgesYetToBeLabelled.set(numberOfEdges);
        Vertex.numberOfVerticesYetActive.set(numberOfVertices);
        Vertex.numberOfForests.set(0);

        for(Vertex vertex : vertices) {
            vertex.init(eps, arboricity, numberOfVertices);
        }
    }

    @Override
    public void simulate() throws InterruptedException {
        init();

        List<Thread> threads = new ArrayList<>();
        for (Vertex v : vertices) {
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
