package com.cmw.adaptive;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cmw.adaptive.metrics.ExecutorMetrics;
import com.cmw.adaptive.queue.PriorityTaskQueue;
import com.cmw.adaptive.scaling.ScalingManager;
import com.cmw.adaptive.scaling.ScalingStrategy;
import com.cmw.adaptive.task.TrackedTask;
import com.cmw.adaptive.worker.Worker;

/**
 * Adaptive task executor capable of executing prioritized tasks while
 * dynamically scaling its worker pool according to a configurable
 * scaling strategy.
 *
 * <p>
 * Features:
 * </p>
 * <ul>
 * <li>Priority-based task scheduling</li>
 * <li>Automatic worker scaling</li>
 * <li>Runtime metrics collection</li>
 * <li>Multiple scaling strategies support</li>
 * </ul>
 *
 * <p>
 * This class is thread-safe and can be shared across multiple producer
 * threads.
 * </p>
 */
public class AdaptiveExecutor {

    private final PriorityTaskQueue queue = new PriorityTaskQueue();

    private final AtomicBoolean running = new AtomicBoolean(true);

    private final List<Thread> workers = new CopyOnWriteArrayList<>();

    private final ExecutorMetrics metrics = new ExecutorMetrics();

    private final int minWorkers;
    private final int maxWorkers;

    private final Thread scalingThread;

    /**
     * Creates a new adaptive executor.
     *
     * @param minWorkers minimum number of workers that should always remain active
     * @param maxWorkers maximum number of workers allowed
     * @param strategy   scaling strategy used to determine desired worker count
     */
    public AdaptiveExecutor(
            int minWorkers,
            int maxWorkers,
            ScalingStrategy strategy) {

        this.minWorkers = minWorkers;
        this.maxWorkers = maxWorkers;

        for (int i = 0; i < minWorkers; i++) {
            createWorker();
        }

        scalingThread = Thread.ofPlatform()
                .name("adaptive-scaler")
                .start(
                        new ScalingManager(
                                this,
                                running,
                                strategy));
    }

    /**
     * Creates and starts a new worker thread.
     *
     * <p>
     * This method is primarily intended for internal use by scaling
     * components.
     * </p>
     */

    public void createWorker() {

        int workerId = workers.size();

        Thread workerThread = Thread.ofPlatform()
                .name("adaptive-worker-" + workerId)
                .start(
                        new Worker(
                                queue,
                                running,
                                metrics));

        workers.add(workerThread);

        metrics.workerStarted();
    }

    /**
     * Removes a worker from the pool if the minimum worker limit
     * has not been reached.
     */
    public void removeWorker() {

        if (workers.size() <= minWorkers) {
            return;
        }

        Thread worker = workers.remove(workers.size() - 1);

        worker.interrupt();

        metrics.workerStopped();
    }

    /**
     * Submits a task for execution.
     *
     * <p>
     * The task will be enqueued according to its priority and executed
     * by available workers.
     * </p>
     *
     * @param task task to execute
     */
    public void submit(
            TrackedTask task) {

        metrics.incrementSubmitted();

        queue.offer(task);

        metrics.updateQueueSize(
                queue.size());
    }

    /**
     * Returns the runtime metrics instance associated with this executor.
     *
     * @return executor metrics
     */
    public ExecutorMetrics metrics() {
        return metrics;
    }

    /**
     * Returns the current number of tasks waiting in the queue.
     *
     * @return current queue size
     */
    public int queueSize() {
        return queue.size();
    }

    /**
     * Returns the current number of active worker threads.
     *
     * @return current worker count
     */
    public int workerCount() {
        return workers.size();
    }

    /**
     * Returns the minimum number of workers that should always remain active.
     *
     * @return minimum worker count
     */
    public int minWorkers() {
        return minWorkers;
    }

    /**
     * Returns the maximum number of workers allowed in this executor.
     *
     * @return maximum worker count
     */
    public int maxWorkers() {
        return maxWorkers;
    }

    /**
     * Gracefully shuts down the executor.
     *
     * <p>
     * Stops the scaling manager and waits for all workers
     * to terminate.
     * </p>
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public void shutdown()
            throws InterruptedException {

        running.set(false);

        scalingThread.interrupt();
        scalingThread.join();

        for (Thread worker : workers) {
            worker.interrupt();
        }

        for (Thread worker : workers) {
            worker.join();
        }
    }
}