package com.cmw.adaptive.worker;

import java.util.concurrent.atomic.AtomicBoolean;

import com.cmw.adaptive.metrics.ExecutorMetrics;
import com.cmw.adaptive.queue.PriorityTaskQueue;
import com.cmw.adaptive.task.AdaptiveTask;
import com.cmw.adaptive.task.TrackedTask;

public class Worker implements Runnable {

    private final PriorityTaskQueue queue;
    private final AtomicBoolean running;
    private final ExecutorMetrics metrics;

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