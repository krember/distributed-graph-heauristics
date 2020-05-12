package com.graphs;

public class Edge {
    private Vertex first;
    private Vertex second;
    private Vertex pointsFrom;
    private Vertex pointsTo;

    public Edge() {}

    public Edge(Vertex first, Vertex second) {
        this.first = first;
        this.second= second;
    }

    public Vertex getFirst() {
        return first;
    }

    public void setFirst(Vertex first) {
        this.first = first;
    }

    public Vertex getSecond() {
        return second;
    }

    public void setSecond(Vertex second) {
        this.second = second;
    }

    public Vertex getPointsFrom() {
        return pointsFrom;
    }

    public void setPointsFrom(Vertex pointsFrom) {
        this.pointsFrom = pointsFrom;
    }

    public Vertex getPointsTo() {
        return pointsTo;
    }

    public void setPointsTo(Vertex pointsTo) {
        this.pointsTo = pointsTo;
    }
}
