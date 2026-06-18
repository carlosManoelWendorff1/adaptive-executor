package com.cmw.adaptive.scaling;

public interface ScalingStrategy {

    int desiredWorkers(
            int queueSize,
            int currentWorkers,
            int minWorkers,
            int maxWorkers);
}