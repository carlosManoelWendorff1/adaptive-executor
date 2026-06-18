package com.cmw.adaptive.scaling;

public class ThresholdScalingStrategy
        implements ScalingStrategy {

    @Override
    public int desiredWorkers(
            int queueSize,
            int currentWorkers,
            int minWorkers,
            int maxWorkers) {

        int desiredWorkers = Math.max(
                minWorkers,
                (queueSize / 5) + 1);

        return Math.min(
                desiredWorkers,
                maxWorkers);
    }
}