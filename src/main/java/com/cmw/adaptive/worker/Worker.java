package com.cmw.adaptive.worker;

import java.util.concurrent.atomic.AtomicBoolean;

import com.cmw.adaptive.metrics.ExecutorMetrics;
import com.cmw.adaptive.queue.PriorityTaskQueue;
import com.cmw.adaptive.task.AdaptiveTask;
import com.cmw.adaptive.task.TrackedTask;

/**
 * Worker thread responsible for executing tasks from the AdaptiveExecutor's
 * priority queue.
 *
 * <p>
 * Each worker continuously retrieves tasks from the queue and executes them,
 * while updating the executor's metrics. The worker checks the running flag
 * to determine when to stop gracefully.
 * </p>
 */
public class Worker implements Runnable {

    private final PriorityTaskQueue queue;
    private final AtomicBoolean running;
    private final ExecutorMetrics metrics;

    /**
     * Creates a new Worker with the given task queue, running flag, and metrics.
     *
     * @param queue   the priority task queue to retrieve tasks from
     * @param running atomic boolean flag to control the worker's lifecycle
     * @param metrics executor metrics instance for recording task execution data
     */
    public Worker(
            PriorityTaskQueue queue,
            AtomicBoolean running,
            ExecutorMetrics metrics) {

        this.queue = queue;
        this.running = running;
        this.metrics = metrics;
    }

    @Override
    public void run() {

        while (running.get()) {

            try {

                TrackedTask trackedTask = queue.take();
                AdaptiveTask task = trackedTask.task();

                metrics.workerBusy();

                long start = System.nanoTime();

                try {

                    task.run();

                    metrics.incrementCompleted();

                } catch (Exception e) {

                    metrics.incrementFailed();

                } finally {

                    long executionTime = System.nanoTime() - start;

                    metrics.recordExecutionTime(
                            executionTime);

                    metrics.workerIdle();
                }

            } catch (InterruptedException e) {

                Thread.currentThread()
                        .interrupt();

                break;
            }
        }
    }
}