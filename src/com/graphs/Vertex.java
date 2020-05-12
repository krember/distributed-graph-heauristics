package com.graphs;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


/**
 * Note that in local case there is no need to mark a neighbor inactive. It's necessary however in case different machines are used
 */
public class Vertex {

    private List<Vertex> neighbors;
    private Set<Edge> outgoingEdges = new TreeSet<Edge>(Comparator.comparing(it -> it.getPointsTo().getId()));
    private long id;
    private long partitionId;
    private AtomicBoolean isActive = new AtomicBoolean(true);
    private final LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();

    public Vertex(List<Vertex> neighbors, long id) {
        this.neighbors = neighbors;
        this.id = id;
    }

    public void addOutgoingEdge(Edge e) {
        this.outgoingEdges.add(e);
    }

    public void setPartitionId(long partitionId) {
        this.partitionId = partitionId;
    }

    public long getPartitionId() {
        return this.partitionId;
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

    public void sendMessage(Message msg) {
        try {
            messages.put(msg);
        } catch (InterruptedException ignored) {

        }
    }

    public void task(int numRound, double eps, double a, List<List<Vertex>> sets) {
        new Thread(() -> {
            new Reader(neighbors, messages);
        }).start();

        for (int i=0; i<numRound; i++) {
            if (isActive.get() && neighbors.stream().filter(Vertex::isActive).count() <= (2+eps*a)) {
                setInactive();
                sets.get(i).add(this);
                setPartitionId(i);

                int finalI = i;
                neighbors.forEach(it -> {
                    Message m1 = new Message("inactive", this.id);
                    Message m2 = new Message(this.id + " joined to H" + finalI, this.id);
                    it.sendMessage(m1);
                    it.sendMessage(m2);
                });
            }
            // for the second if the reader has already marked
        }
    }

}
