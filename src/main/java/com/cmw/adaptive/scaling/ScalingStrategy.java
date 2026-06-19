package com.cmw.adaptive.scaling;

/**
 * Interface for defining scaling strategies used by the AdaptiveExecutor.
 *
 * <p>
 * Implementations of this interface provide logic to determine the desired
 * number of worker threads based on the current state of the executor,
 * such as queue size, task execution times, and other metrics.
 * </p>
 */
public interface ScalingStrategy {
    /**
     * Calculates the desired number of worker threads based on the provided
     * scaling context.
     *
     * @param context current state of the executor used for scaling decisions
     *
     * @return desired worker count
     */
    int desiredWorkers(ScalingContext context);
}