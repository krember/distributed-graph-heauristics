package com.graphs.simulators;

import com.graphs.Vertex;
import com.graphs.simulators.util.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlanarGraphSimulator implements Simulator {
    private static final double MAX_COORD = 10;
    private static final double MIN_COORD = 0;
    private static final long ARBORICITY_UPPER_BOUND = 3;

    private List<Vertex> vertices = new ArrayList<>();
    private Map<Vertex, Point> coordinates = new HashMap<>();

    private int numberOfVertices;
    private double eps;
    private int numberOfEdges;

    public PlanarGraphSimulator(int numberOfVertices, double eps) {
        this.numberOfVertices = numberOfVertices;
        this.eps = eps;
        this.numberOfEdges = 0;

        Random rand = new Random();

        for (int i = 1; i <= numberOfVertices; ++i) {
            Vertex vertex = new Vertex(i);
            vertices.add(vertex);

            double x = MIN_COORD + (Math.random() * (MAX_COORD - MIN_COORD));
            double y = MIN_COORD + (Math.random() * (MAX_COORD - MIN_COORD));

            coordinates.put(vertex, new Point(x, y));
        }

        for (int u = 0; u < numberOfVertices; ++u) {
            for (int v = u + 1; v < numberOfVertices; ++v) {
                Vertex vertexU = vertices.get(u);
                Vertex vertexV = vertices.get(v);

                boolean isIn = false;
                if (coordinates.get(vertexU).isInsideUnitDisk(coordinates.get(vertexV))) {
                    isIn = true;
                    for (int w = 0; w < numberOfVertices; ++w) {
                        if (w == u || w == v) {
                            continue;
                        }
                        Vertex vertexW = vertices.get(w);

                        double distanceUW = coordinates.get(vertexU).distance(coordinates.get(vertexW));
                        double distanceVW = coordinates.get(vertexV).distance(coordinates.get(vertexW));
                        double distanceUV = coordinates.get(vertexV).distance(coordinates.get(vertexU));

                        if (distanceUW < distanceUV && distanceVW < distanceUV) {
                            isIn = false;
                        }
                    }
                }

                if(isIn) {
                    vertexU.addNeighbor(vertexV);
                    vertexV.addNeighbor(vertexU);
                    ++numberOfEdges;
                }
            }
        }
    }

    private void init() {
        Vertex.numberOfEdgesYetToBeLabelled.set(numberOfEdges);
        Vertex.numberOfVerticesYetActive.set(numberOfVertices);
        Vertex.numberOfForests.set(0);

        for(Vertex vertex : vertices) {
            vertex.init(eps, ARBORICITY_UPPER_BOUND, numberOfVertices);
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
        System.out.println("Arboricity Upper Bound      : " + ARBORICITY_UPPER_BOUND);
        System.out.println("Number of decomposed forests: " + Vertex.numberOfForests.get());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
