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

public class AdaptiveExecutor {

    private final PriorityTaskQueue queue = new PriorityTaskQueue();

    private final AtomicBoolean running = new AtomicBoolean(true);

    private final List<Thread> workers = new CopyOnWriteArrayList<>();

    private final ExecutorMetrics metrics = new ExecutorMetrics();

    private final int minWorkers;
    private final int maxWorkers;

    private final Thread scalingThread;

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

    public void removeWorker() {

        if (workers.size() <= minWorkers) {
            return;
        }

        Thread worker = workers.remove(workers.size() - 1);

        worker.interrupt();

        metrics.workerStopped();
    }

    public void submit(
            TrackedTask task) {

        metrics.incrementSubmitted();

        queue.offer(task);

        metrics.updateQueueSize(
                queue.size());
    }

    public ExecutorMetrics metrics() {
        return metrics;
    }

    public int queueSize() {
        return queue.size();
    }

    public int workerCount() {
        return workers.size();
    }

    public int minWorkers() {
        return minWorkers;
    }

    public int maxWorkers() {
        return maxWorkers;
    }

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