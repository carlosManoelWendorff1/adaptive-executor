package com.cmw.adaptive.scaling;

import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cmw.adaptive.AdaptiveExecutor;
import com.sun.management.OperatingSystemMXBean;

/**
 * Manages the dynamic scaling of worker threads in the AdaptiveExecutor based
 * on a provided ScalingStrategy.
 *
 * <p>
 * The ScalingManager runs in a separate thread and periodically evaluates the
 * current state of the executor, including queue size, active workers, CPU
 * load, and task throughput. It then uses the ScalingStrategy to determine
 * whether to scale up or down the number of worker threads.
 * </p>
 */
public class ScalingManager implements Runnable {

    private static final long CHECK_INTERVAL_MS = 200;
    private static final long COOLDOWN_MS = 1000;

    private final AdaptiveExecutor executor;
    private final AtomicBoolean running;
    private final ScalingStrategy strategy;
    private final OperatingSystemMXBean os;

    private long lastScalingAction;
    private long previousCompletedTasks;
    private double previousThroughputPerWorker;

    /**
     * Creates a new ScalingManager for the given executor and scaling strategy.
     *
     * @param executor the AdaptiveExecutor to manage
     * @param running  atomic boolean flag to control the lifecycle of the manager
     *                 thread
     * @param strategy scaling strategy used to determine desired worker count
     */
    public ScalingManager(
            AdaptiveExecutor executor,
            AtomicBoolean running,
            ScalingStrategy strategy) {

        this.executor = executor;
        this.running = running;
        this.strategy = strategy;
        this.os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public void run() {

        while (running.get()) {

            try {

                evaluate();

                Thread.sleep(CHECK_INTERVAL_MS);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void evaluate() {

        if (cooldownActive()) {
            return;
        }

        int queueSize = executor.queueSize();
        int currentWorkers = executor.workerCount();
        int activeWorkers = executor.metrics().activeWorkers();

        double cpuLoad = Math.max(0.0, os.getCpuLoad());

        // throughput por worker neste ciclo
        long completedNow = executor.metrics().completedTasks();
        long completedDelta = completedNow - previousCompletedTasks;
        previousCompletedTasks = completedNow;

        double throughputPerWorker = activeWorkers > 0
                ? (double) completedDelta / activeWorkers
                : 0.0;

        ScalingContext ctx = new ScalingContext(
                queueSize,
                currentWorkers,
                activeWorkers,
                executor.minWorkers(),
                executor.maxWorkers(),
                cpuLoad,
                throughputPerWorker,
                previousThroughputPerWorker,
                executor.metrics().averageExecutionTimeMillis());

        previousThroughputPerWorker = throughputPerWorker;

        int desiredWorkers = strategy.desiredWorkers(ctx);

        if (desiredWorkers > currentWorkers) {

            int amount = desiredWorkers - currentWorkers;

            for (int i = 0; i < amount; i++) {
                executor.createWorker();
            }

            lastScalingAction = System.currentTimeMillis();

            System.out.printf(
                    "[SCALER] Scale UP -> workers=%d queue=%d cpu=%.0f%%%n",
                    executor.workerCount(),
                    queueSize,
                    cpuLoad * 100);

            return;
        }

        if (desiredWorkers < currentWorkers) {

            int activeAndQueued = queueSize + activeWorkers;

            if (activeAndQueued >= currentWorkers) {
                return;
            }

            // desce no máximo 2 workers por ciclo — evita queda brusca
            int maxScaleDown = 2;
            int amount = Math.min(
                    currentWorkers - desiredWorkers,
                    maxScaleDown);

            for (int i = 0; i < amount; i++) {
                executor.removeWorker();
            }

            lastScalingAction = System.currentTimeMillis();

            System.out.printf(
                    "[SCALER] Scale DOWN -> workers=%d queue=%d cpu=%.0f%%%n",
                    executor.workerCount(),
                    queueSize,
                    cpuLoad * 100);
        }
    }

    private boolean cooldownActive() {
        return System.currentTimeMillis() - lastScalingAction < COOLDOWN_MS;
    }
}