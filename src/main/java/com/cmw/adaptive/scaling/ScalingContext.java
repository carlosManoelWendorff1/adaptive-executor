package com.cmw.adaptive.scaling;

/**
 * Immutable context object containing metrics and state information
 * used by the {@link ScalingStrategy} to make scaling decisions.
 *
 * <p>
 * This record encapsulates all relevant data about the executor's current
 * state, such as queue size, worker counts, CPU load, and throughput metrics.
 * It is passed to the scaling strategy whenever a scaling decision needs to be
 * made.
 * </p>
 *
 * @param queueSize                   current number of tasks waiting in the
 *                                    queue
 * @param currentWorkers              current total number of worker threads
 * @param activeWorkers               number of worker threads currently
 *                                    executing tasks
 * @param minWorkers                  minimum allowed number of worker threads
 * @param maxWorkers                  maximum allowed number of worker threads
 * @param cpuLoad                     current CPU load as a percentage (0.0 to
 *                                    100.0)
 * @param throughputPerWorker         current average throughput per worker
 *                                    (tasks/sec)
 * @param previousThroughputPerWorker previous average throughput per worker
 *                                    (tasks/sec) from the last scaling check
 * @param avgExecutionTimeMillis      average execution time of recent tasks in
 *                                    milliseconds
 */
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