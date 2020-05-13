package com.graphs;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


/**
 * Note that in local case there is no need to mark arboricity neighbor inactive. It's necessary however in case different machines are used
 */
public class Vertex implements Runnable {
    public static AtomicInteger numberOfEdgesYetToBeLabelled = new AtomicInteger();
    public static AtomicInteger numberOfVerticesYetActive = new AtomicInteger();

    public static final AtomicInteger numberOfForests = new AtomicInteger();

    private static final Logger logger = Logger.getLogger(Thread.currentThread().getName());

    private Set<Vertex> neighbors = new HashSet<>();
    private Set<Edge> outgoingEdges = new TreeSet<>(Comparator.comparing(it -> it.getDestination().getId()));
    private long id;
    private long hPartitionId;
    private AtomicBoolean isActive = new AtomicBoolean(true);
    private final LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();

    private double eps;
    private double arboricity;
    private int numRounds;

    public Vertex(long id) {
        this.id = id;
    }

    public void init(double eps, long arboricity, long n) {
        this.eps = eps;
        this.arboricity = arboricity;
        this.numRounds = (int) Math.floor(Math.log(n) * 2 / eps);
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

        hPartition(eps, arboricity);

        Thread.sleep(5000);
        while (neighbors.stream().anyMatch(Vertex::isActive)) {
            Thread.sleep(1000);
        }
        orientation();

        Thread.sleep(5000);
        labelOutgoingEdges();
    }

    private void hPartition(double eps, double a) throws InterruptedException {
        for (int i = 0; i < numRounds; i++) {
            if (isActive.get() && neighbors.stream().filter(Vertex::isActive).count() <= (2 + eps) * a) {
                System.out.println("Number of vertices yet active: " + numberOfVerticesYetActive.decrementAndGet());
                setInactive();
                setHPartitionId(i);

                int finalI = i;
                System.out.println("Message broadcasted -> Content: \"joined to H" + finalI + "\" , Source: " + id);
//                System.out.println("Message sent -> Content: \"inactive\" , Source: " + id);
                neighbors.forEach(it -> {
                    Message m1 = new Message("inactive", this.id);
                    Message m2 = new Message(this.id + " joined to H" + finalI, this.id);
                    it.sendMessage(m1);
                    it.sendMessage(m2);
                });
            }
            Thread.sleep(5);
        }
    }

    private void labelOutgoingEdges() {
        int count = 0;

        for (Edge e : outgoingEdges) {
//            logger.info("Edge (" + e.getSource().getId() + ", " + e.getDestination().getId() + ") have been assigned label: " + count);

            System.out.println("Edge (" + e.getSource().getId() + ", " + e.getDestination().getId() + ") has been added to the forest: " + count);
            e.setLabel(count++);
            System.out.println("Number of edges yet to be labelled: " + numberOfEdgesYetToBeLabelled.decrementAndGet());
        }

        synchronized (numberOfForests) {
            if(numberOfForests.get() < count) {
                numberOfForests.set(count);
            }
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
            } else if (firstPartitionId == secondPartitionId && firstId < secondId) {
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

    // Utility: not used in the algorithm
    public int degree() {
        return neighbors.size();
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
