package com.graphs;

import javax.print.attribute.standard.RequestingUserName;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Callable;
import java.util.logging.Logger;


/**
 * Note that in local case there is no need to mark a neighbor inactive. It's necessary however in case different machines are used
 */
public class Vertex implements Runnable {
    private static final Logger logger = Logger.getLogger(Thread.currentThread().getName());

    private Set<Vertex> neighbors = new HashSet<>();
    private Set<Edge> outgoingEdges = new TreeSet<>(Comparator.comparing(it -> it.getDestination().getId()));
    private long id;
    private long hPartitionId;
    private AtomicBoolean isActive = new AtomicBoolean(true);
    private final LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();

    private double eps;
    private double a;

    public Vertex(long id, double eps, long a) {
        this.id = id;
        this.eps = eps;
        this.a = a;
    }

    public void sendMessage(Message msg) {
        try {
            messages.put(msg);
        } catch (InterruptedException ignored) {

        }
    }

    public void task() throws InterruptedException {
        new Thread(() -> {
            new Reader(neighbors, messages);
        }).start();

        int numRound = 19;
        hPartition(numRound, eps, a);

        if (neighbors.stream().noneMatch(Vertex::isActive)) {
            orientation();
            labelOutgoingEdges();
        }
    }

    private void hPartition(int numRound, double eps, double a) throws InterruptedException {
        for (int i = 0; i < numRound; i++) {
            if (isActive.get() && neighbors.stream().filter(Vertex::isActive).count() <= (2 + eps) * a) {
                setInactive();
                setHPartitionId(i);

                int finalI = i;
                neighbors.forEach(it -> {
                    Message m1 = new Message("inactive", this.id);
                    Message m2 = new Message(this.id + " joined to H" + finalI, this.id);
                    it.sendMessage(m1);
                    it.sendMessage(m2);
                });
            }
            Thread.sleep(1000); //TODO
            // for the second if the reader has already marked
        }
    }

    private void labelOutgoingEdges() {
        int count = 0;

        for (Edge e : outgoingEdges) {
//            logger.info("Edge (" + e.getSource().getId() + ", " + e.getDestination().getId() + ") have been assigned label: " + count);
            System.out.println("Edge (" + e.getSource().getId() + ", " + e.getDestination().getId() + ") has been added to the forest: " + count);
            e.setLabel(count++);
        }
    }

    private void orientation() {
        neighbors.forEach(neighbor -> {
            long firstPartitionId = hPartitionId;
            long secondPartitionId = neighbor.getHPartitionId();

            long firstId = id;
            long secondId = neighbor.getId();

            Edge edge;
            if (firstPartitionId < secondPartitionId) {
                edge = new Edge(this, neighbor);
                this.addOutgoingEdge(edge);
//                logger.info("Edge (" + edge.getSource().getId() + ", " + edge.getDestination().getId() + ") has been oriented!");
                System.out.println("Edge (" + edge.getSource().getId() + ", " + edge.getDestination().getId() + ") has been oriented!");
            } else if (firstId < secondId) {
                edge = new Edge(this, neighbor);
                this.addOutgoingEdge(edge);
//                logger.info("Edge (" + edge.getSource().getId() + ", " + edge.getDestination().getId() + ") has been oriented!");
                System.out.println("Edge (" + edge.getSource().getId() + ", " + edge.getDestination().getId() + ") has been oriented!");
            }
        });
    }


    // Getters & Setters

    public void addNeighbor(Vertex vertex) {
        neighbors.add(vertex);
    }

    public void addNeighbors(List<Vertex> vertices) {
        neighbors.addAll(vertices);
    }

    public void addOutgoingEdge(Edge e) {
        this.outgoingEdges.add(e);
    }

    public void setHPartitionId(long hPartitionId) {
        this.hPartitionId = hPartitionId;
    }

    public long getHPartitionId() {
        return this.hPartitionId;
    }

    public long getId() {
        return this.id;
    }

    public void setInactive() {
        isActive.set(false);
    }

    public boolean isActive() {
        return this.isActive.get();
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
