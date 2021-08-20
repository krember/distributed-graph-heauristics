package com.graphs.coloring.simulators;

import com.graphs.coloring.Edge;
import com.graphs.common.simulators.Simulator;
import com.graphs.coloring.Vertex;

import java.util.*;

public class AcyclicTournamentSimulator implements Simulator {
    private static final Random random = new Random();
    private static int cycleCheckTimer = 0;

    private final List<Vertex> vertices1 = new ArrayList<>();
    private final List<Vertex> vertices2 = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private int arboricity;
    private final int numberOfVertices;
    private final double vertexProbability;
    private final double edgeProbability;
    private final int iterationsLimit;
    private int iterations;
    private boolean verbose;
    private boolean success;

    public AcyclicTournamentSimulator(int numberOfVertices, int iterationsLimit) {
        this(numberOfVertices, iterationsLimit, 0.5, 0.8, false);
    }

    public AcyclicTournamentSimulator(int numberOfVertices, int iterationsLimit, double vertexProbability, double edgeProbability, boolean verbose) {
        this.numberOfVertices = numberOfVertices;
        this.vertexProbability = vertexProbability;
        this.edgeProbability = edgeProbability;
        this.iterationsLimit = iterationsLimit;
        this.verbose = verbose;

        for (int i = 1; i <= numberOfVertices; ++i) {
            long color = random.nextDouble() < vertexProbability ? 1 : 2;

            if (color == 1) {
                vertices1.add(new Vertex(i));
            } else {
                vertices2.add(new Vertex(i));
            }
        }

        addEdges(vertices1);
        addEdges(vertices2);
        addEdges(vertices2, vertices1);
    }

    private void addEdges(List<Vertex> sources, List<Vertex> destinations) {
        for (Vertex source : sources) {
            for (Vertex destination : destinations) {
                boolean addEdge = random.nextDouble() < edgeProbability;
                boolean orient = random.nextBoolean();

                if (addEdge) {
                    Edge edge;
                    if (orient) {
                        edge = new Edge(source, destination);
                        source.addOutgoingEdge(destination);
                        destination.addIngoingEdge(source);
                    } else {
                        edge = new Edge(destination, source);
                        source.addIngoingEdge(destination);
                        destination.addOutgoingEdge(source);
                    }
                    edges.add(edge);
                }
            }
        }
    }

    private void addEdges(List<Vertex> sources) {
        for (int i = 0; i < sources.size(); ++i) {
            for (int j = i + 1; j < sources.size(); ++j) {
                boolean addEdge = random.nextDouble() < edgeProbability;

                if (addEdge) {
                    Vertex v1 = sources.get(i);
                    Vertex v2 = sources.get(j);
                    Edge edge = new Edge(v1, v2);
                    v1.addOutgoingEdge(v2);
                    v2.addIngoingEdge(v1);
                    edges.add(edge);
                }
            }
        }
    }

    private void init() {
        for (Vertex vertex : vertices1) {
            vertex.setColor(random.nextInt(2) + 1);
        }
        for (Vertex vertex : vertices2) {
            vertex.setColor(random.nextInt(2) + 1);
        }
    }

    @Override
    public void simulate() throws InterruptedException {
        init();

        for (iterations = 0; iterations < iterationsLimit; ++iterations) {
            int index = random.nextInt(numberOfVertices);

            Vertex vertex;
            if (index < vertices1.size()) {
                vertex = vertices1.get(index);
            } else {
                vertex = vertices2.get(index - vertices1.size());
            }
            vertex.run();

            if (!checkCycle(false)) {
                break;
            }
        }

        if (!checkCycle(true)) {
            success = true;
            if (verbose) {
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Colored Successfully. Iterations: " + iterations);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            }
        } else {
            success = false;
            if (verbose) {
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Coloring failed.");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            }
        }
    }

    private boolean checkCycle(boolean force) {
        ++cycleCheckTimer;
        if (cycleCheckTimer % (int)(Math.pow(numberOfVertices, 0.8)) != 0 && !force) {
            return true;
        }

        for (Edge e : edges) {
            long color;
            if ((color = e.getSource().getColor()) == e.getDestination().getColor()) {
                boolean hasTriangle = e.getSource().getIngoingEdgeVertices().
                        stream().
                        filter(v -> v.getColor() == color).
                        anyMatch(v -> e.getDestination().
                                getOutgoingEdgeVertices().
                                stream().filter(u -> u.getColor() == color).
                                anyMatch(u -> u == v));
                if (hasTriangle) {
                    return true;
                }
            }
        }

//        for (Vertex v : vertices1 ){
//            if(v.checkCycle()) {
//                return true;
//            }
//        }
//
//        for(Vertex v : vertices2) {
//            if(v.checkCycle()) {
//                return true;
//            }
//        }

        return false;
    }

    @Override
    public int getIterations() {
        return iterations;
    }

    public boolean isSuccess() {
        return success;
    }
}
