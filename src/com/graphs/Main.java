package com.graphs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    public static void main(String[] args) {
	// write your code here
        //these are the H sets
       int l = 3;
       List<CopyOnWriteArrayList<Vertex>> sets = new ArrayList<>();
       for (int i=0; i<l; i++) {
           sets.add(new CopyOnWriteArrayList<>());
       }

    }

    public static void orientation(List<Edge> edges) {
        edges.forEach(it -> {
            long firstPartitionId = it.getFirst().getPartitionId();
            long secondPartitionId = it.getSecond().getPartitionId();
            if (firstPartitionId < secondPartitionId) {
                it.setPointsFrom(it.getFirst());
                it.setPointsTo(it.getSecond());
                it.getFirst().addOutgoingEdge(it);
            } else if (secondPartitionId < firstPartitionId) {
                it.setPointsFrom(it.getSecond());
                it.setPointsTo(it.getFirst());
                it.getSecond().addOutgoingEdge(it);
            } else {
                if (it.getFirst().getId() < it.getSecond().getId()) {
                    it.setPointsFrom(it.getFirst());
                    it.setPointsTo(it.getSecond());
                    it.getFirst().addOutgoingEdge(it);
                } else {
                    it.setPointsFrom(it.getSecond());
                    it.setPointsTo(it.getFirst());
                    it.getSecond().addOutgoingEdge(it);
                }
            }
        });
    }

//    /**
//     *
//     * @param a
//     * @param eps
//     * @param n number of vertices
//     */
//    public static void partition(double a, double eps, int n) {
//
//        int l = (int) Math.floor((2/eps) * Math.log(n));
//
//    }

//    move this method to vertex class
//    public static void vertexTask() {
//
//    }
}
