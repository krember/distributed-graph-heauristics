package com.graphs;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here
        List<List<Vertex>> sets = List.of(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

    }

    /**
     *
     * @param a
     * @param eps
     * @param n number of vertices
     */
    public static void partition(double a, double eps, int n) {

        int l = (int) Math.floor((2/eps) * Math.log(n));

    }

//    move this method to vertex class
//    public static void vertexTask() {
//
//    }
}
