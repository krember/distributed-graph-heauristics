package com.graphs.common.simulators.util;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Point otherPoint) {
        return Math.sqrt((x - otherPoint.x) * (x - otherPoint.x) + (y - otherPoint.y) * (y - otherPoint.y));
    }

    public boolean isInsideUnitDisk(Point otherPoint) {
        return distance(otherPoint) <= 1;
    }
}
