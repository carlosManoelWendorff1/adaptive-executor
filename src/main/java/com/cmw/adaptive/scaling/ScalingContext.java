package com.cmw.adaptive.scaling;

public record ScalingContext(
        int queueSize,
        int currentWorkers,
        int activeWorkers,
        int minWorkers,
        int maxWorkers,
        double cpuLoad,
        double throughputPerWorker,
        double previousThroughputPerWorker,
        double avgExecutionTimeMillis) {
}