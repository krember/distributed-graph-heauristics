package com.graphs.simulators;

import com.graphs.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSimulator implements Simulator {
    private List<Vertex> vertices = new ArrayList<>();
    private int estimatedArboricity;
    private int numberOfVertices;
    private double eps;

    public RandomSimulator(int numberOfVertices, int estimatedNumberOfEdges, double eps) {
        this.numberOfVertices = numberOfVertices;
        this.eps = eps;

        Random rand = new Random();

        for (int i = 1; i <= numberOfVertices; ++i) {
            vertices.add(new Vertex(i));
        }

        for (long i = 0; i < estimatedNumberOfEdges; ++i) {
            int source = Math.abs(rand.nextInt() % numberOfVertices);
            int target = Math.abs(rand.nextInt() % numberOfVertices);

            if (source != target) {
                vertices.get(source).addNeighbor(vertices.get(target));
                vertices.get(target).addNeighbor(vertices.get(source));
            }
        }
    }

    private void init() {
        int numberOfEdges = 0;
        for(Vertex vertex : vertices) {
            numberOfEdges += vertex.degree();
        }
        numberOfEdges /= 2;
        estimatedArboricity = (int) Math.ceil(numberOfEdges * 1.0 / (numberOfVertices - 1));

        Vertex.numberOfEdgesYetToBeLabelled.set(numberOfEdges);
        Vertex.numberOfVerticesYetActive.set(numberOfVertices);
        Vertex.numberOfForests.set(0);

        for(Vertex vertex : vertices) {
            vertex.init(eps, estimatedArboricity, numberOfVertices);
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
        System.out.println("Arboricity Lower Bound      : " + estimatedArboricity);
        System.out.println("Number of decomposed forests: " + Vertex.numberOfForests.get());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
