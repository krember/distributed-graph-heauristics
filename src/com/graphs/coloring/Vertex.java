package com.graphs.coloring;

import com.sun.nio.sctp.PeerAddressChangeNotification;

import java.util.*;
import java.util.logging.Logger;

/**
 * Note that in local case there is no need to mark arboricity neighbor inactive. It's necessary however in case different machines are used
 */
public class Vertex implements Runnable {
    private static final Random random = new Random();
    private static final Logger logger = Logger.getLogger(Thread.currentThread().getName());

    private final Set<Vertex> outgoingEdgeVertices = new HashSet<>();
    private final Set<Vertex> ingoingEdgeVertices = new HashSet<>();
    
    private final long id;
    private long color;

    public Vertex(long id) {
        this.id = id;
    }

    public void task() throws InterruptedException {
        final Set<Vertex> outgoingColor1 = new HashSet<>();
        final Set<Vertex> ingoingColor1 = new HashSet<>();
        final Set<Vertex> outgoingColor2 = new HashSet<>();
        final Set<Vertex> ingoingColor2 = new HashSet<>();

        for (Vertex v : outgoingEdgeVertices) {
            if (v.getColor() == 1) {
                outgoingColor1.add(v);
            } else {
                outgoingColor2.add(v);
            }
        }
        for (Vertex v : ingoingEdgeVertices) {
            if (v.getColor() == 1) {
                ingoingColor1.add(v);
            } else {
                ingoingColor2.add(v);
            }
        }

        long numberOfCycles1 = 0;
        for (Vertex v : outgoingColor1) {
            for (Vertex u : v.getOutgoingEdgeVertices()) {
                if (ingoingColor1.contains(u)) {
                    ++numberOfCycles1;
                }
            }
        }

        long numberOfCycles2 = 0;
        for (Vertex v : outgoingColor2) {
            for (Vertex u : v.getOutgoingEdgeVertices()) {
                if (ingoingColor2.contains(u)) {
                    ++numberOfCycles2;
                }
            }
        }

        boolean changeTo1;
//        boolean changeTo1 = random.nextDouble() < 1D - (numberOfCycles1 * 1D / (numberOfCycles1 + numberOfCycles2));
        if (numberOfCycles1 == 0) {
            changeTo1 = true;
        } else if (numberOfCycles2 == 0) {
            changeTo1 = false;
        } else {
            double exp1 = Math.pow(7, -numberOfCycles1);
            double exp2 = Math.pow(7, -numberOfCycles2);

            changeTo1 = random.nextDouble() < (exp1 / (exp1 + exp2));
        }

        if (changeTo1) {
            color = 1;
        } else {
            color = 2;
        }
    }
    
    // Getters & Setters
    public void addOutgoingEdge(Vertex v) {
        this.outgoingEdgeVertices.add(v);
    }

    public void addIngoingEdge(Vertex v) {
        this.ingoingEdgeVertices.add(v);
    }

    public Set<Vertex> getOutgoingEdgeVertices() {
        return outgoingEdgeVertices;
    }

    public Set<Vertex> getIngoingEdgeVertices() {
        return ingoingEdgeVertices;
    }

    public long getId() {
        return this.id;
    }

    public long getColor() {
        return color;
    }

    public void setColor(long color) {
        this.color = color;
    }

    public boolean checkCycle() {
        final Set<Vertex> outgoingColor1 = new HashSet<>();
        final Set<Vertex> ingoingColor1 = new HashSet<>();
        final Set<Vertex> outgoingColor2 = new HashSet<>();
        final Set<Vertex> ingoingColor2 = new HashSet<>();

        for (Vertex v : outgoingEdgeVertices) {
            if (v.getColor() == 1) {
                outgoingColor1.add(v);
            } else {
                outgoingColor2.add(v);
            }
        }
        for (Vertex v : ingoingEdgeVertices) {
            if (v.getColor() == 1) {
                ingoingColor1.add(v);
            } else {
                ingoingColor2.add(v);
            }
        }

        long numberOfCycles1 = 0;
        for (Vertex v : outgoingColor1) {
            for (Vertex u : v.getOutgoingEdgeVertices()) {
                if (ingoingColor1.contains(u)) {
                    ++numberOfCycles1;
                }
            }
        }

        long numberOfCycles2 = 0;
        for (Vertex v : outgoingColor2) {
            for (Vertex u : v.getOutgoingEdgeVertices()) {
                if (ingoingColor2.contains(u)) {
                    ++numberOfCycles2;
                }
            }
        }

        return (color == 1 && numberOfCycles1 > 0) || (color == 2 && numberOfCycles2 > 0);
    }

    @Override
    public void run() {
        try {
            task();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
