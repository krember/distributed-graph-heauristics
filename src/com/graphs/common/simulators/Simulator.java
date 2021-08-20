package com.graphs.common.simulators;

public interface Simulator {
    void simulate() throws InterruptedException;
    default int getIterations() {
        return -1;
    };
}
