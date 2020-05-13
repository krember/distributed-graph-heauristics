package com.graphs;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // write your code here
        //these are the H sets
        int l = 3;
        List<CopyOnWriteArrayList<Vertex>> sets = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            sets.add(new CopyOnWriteArrayList<>());
        }

        simulationRandom(50, 110);
    }


    private static void simulationRandom(int numberOfVertices, int estimatedNumberOfEdges) throws InterruptedException {
        List<Vertex> vertices = new ArrayList<>();
        Random rand = new Random();

        for(int i = 1; i <= numberOfVertices; ++i) {
            vertices.add(new Vertex(i, 1, 20));
        }

        for(long i = 0; i < estimatedNumberOfEdges; ++i) {
            int source = Math.abs(rand.nextInt() % numberOfVertices);
            int target = Math.abs(rand.nextInt() % numberOfVertices);

            if(source != target) {
                vertices.get(source).addNeighbor(vertices.get(target));
                vertices.get(target).addNeighbor(vertices.get(source));
            }
        }

        List<Thread> threads = new ArrayList<>();
        for(Vertex v : vertices) {
            Thread thread = new Thread(v);
            threads.add(thread);
            thread.start();
        }

        for(Thread t : threads) {
            t.join();
        }
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
