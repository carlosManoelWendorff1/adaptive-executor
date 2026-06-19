package com.cmw.adaptive.metrics;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collects runtime statistics and performance metrics
 * for an {@link com.cmw.adaptive.AdaptiveExecutor}.
 *
 * <p>
 * Metrics include:
 * </p>
 * <ul>
 * <li>Task statistics</li>
 * <li>Worker statistics</li>
 * <li>Queue statistics</li>
 * <li>Scaling events</li>
 * <li>Execution timings</li>
 * <li>Throughput and utilization</li>
 * </ul>
 *
 * <p>
 * All metrics are maintained using atomic structures and
 * are safe for concurrent access.
 * </p>
 */
public final class ExecutorMetrics {

        /*
         * Tasks
         */
        private final AtomicLong submittedTasks = new AtomicLong();
        private final AtomicLong completedTasks = new AtomicLong();
        private final AtomicLong failedTasks = new AtomicLong();
        private final AtomicLong rejectedTasks = new AtomicLong();

        /*
         * Workers
         */
        private final AtomicInteger activeWorkers = new AtomicInteger();
        private final AtomicInteger currentWorkers = new AtomicInteger();
        private final AtomicInteger peakWorkers = new AtomicInteger();

        private final AtomicLong workersCreated = new AtomicLong();
        private final AtomicLong workersDestroyed = new AtomicLong();

        /*
         * Queue
         */
        private final AtomicInteger currentQueueSize = new AtomicInteger();
        private final AtomicInteger peakQueueSize = new AtomicInteger();

        private final AtomicLong totalQueueWaitTimeNanos = new AtomicLong();
        private final AtomicLong longestQueueWaitTimeNanos = new AtomicLong();

        /*
         * Execution
         */
        private final AtomicLong totalExecutionTimeNanos = new AtomicLong();
        private final AtomicLong longestExecutionTimeNanos = new AtomicLong();

        /*
         * Scaling
         */
        private final AtomicLong scaleUpEvents = new AtomicLong();
        private final AtomicLong scaleDownEvents = new AtomicLong();

        /*
         * Utilization
         */
        private final AtomicLong totalWorkerBusyTimeNanos = new AtomicLong();

        /*
         * Runtime
         */
        private final long executorStartTime = System.nanoTime();

        /*
         * ===== Task Getters =====
         */

        /**
         * Returns the total number of tasks that have been submitted for execution.
         *
         * @return total submitted tasks
         */
        public long submittedTasks() {
                return submittedTasks.get();
        }

        /**
         * Returns the total number of tasks that have completed successfully.
         *
         * @return total completed tasks
         */
        public long completedTasks() {
                return completedTasks.get();
        }

        /**
         * Returns the total number of tasks that have failed during execution.
         *
         * @return total failed tasks
         */
        public long failedTasks() {
                return failedTasks.get();
        }

        /**
         * Returns the total number of tasks that were rejected due to capacity limits.
         *
         * @return total rejected tasks
         */
        public long rejectedTasks() {
                return rejectedTasks.get();
        }

        /*
         * ===== Worker Getters =====
         */

        /**
         * Returns the number of currently active (busy) workers.
         *
         * @return active worker count
         */
        public int activeWorkers() {
                return activeWorkers.get();
        }

        /**
         * Returns the current total number of workers (active + idle).
         *
         * @return current worker count
         */
        public int currentWorkers() {
                return currentWorkers.get();
        }

        /**
         * Returns the peak number of workers that have been active simultaneously.
         *
         * @return peak worker count
         */
        public int peakWorkers() {
                return peakWorkers.get();
        }

        /**
         * Returns the total number of worker threads that have been created since
         * startup.
         *
         * @return total workers created
         */
        public long workersCreated() {
                return workersCreated.get();
        }

        /**
         * Returns the total number of worker threads that have been destroyed since
         * startup.
         *
         * @return total workers destroyed
         */
        public long workersDestroyed() {
                return workersDestroyed.get();
        }

        /*
         * ===== Queue Getters =====
         */

        /**
         * Returns the current number of tasks waiting in the queue.
         *
         * @return current queue size
         */
        public int currentQueueSize() {
                return currentQueueSize.get();
        }

        /**
         * Returns the maximum number of tasks that have been waiting in the queue
         * simultaneously.
         *
         * @return peak queue size
         */
        public int peakQueueSize() {
                return peakQueueSize.get();
        }

        /*
         * ===== Scaling Getters =====
         */

        /**
         * Returns the total number of scale-up events that have occurred since
         * startup.
         *
         * @return total scale-up events
         */
        public long scaleUpEvents() {
                return scaleUpEvents.get();
        }

        /**
         * Returns the total number of scale-down events that have occurred since
         * startup.
         *
         * @return total scale-down events
         */
        public long scaleDownEvents() {
                return scaleDownEvents.get();
        }

        /*
         * ===== Derived Metrics =====
         */

        /**
         * Returns the total execution time of all completed tasks in milliseconds.
         *
         * @return total execution time in milliseconds
         */
        public double totalExecutionTimeMillis() {
                return totalExecutionTimeNanos.get() / 1_000_000.0;
        }

        /**
         * Returns the total time tasks have spent waiting in the queue in milliseconds.
         *
         * @return total queue wait time in milliseconds
         */
        public double totalQueueWaitTimeMillis() {
                return totalQueueWaitTimeNanos.get() / 1_000_000.0;
        }

        /**
         * Returns the average execution time of completed tasks.
         *
         * @return average execution time in milliseconds
         */
        public double averageExecutionTimeMillis() {

                long completed = completedTasks();

                if (completed == 0) {
                        return 0;
                }

                return (totalExecutionTimeNanos.get() / 1_000_000.0)
                                / completed;
        }

        /**
         * Returns the longest execution time among completed tasks.
         *
         * @return longest execution time in milliseconds
         */
        public double longestExecutionTimeMillis() {

                return longestExecutionTimeNanos.get()
                                / 1_000_000.0;
        }

        /**
         * Returns the average time tasks have spent waiting in the queue.
         *
         * @return average queue wait time in milliseconds
         */
        public double averageQueueWaitMillis() {

                long submitted = submittedTasks();

                if (submitted == 0) {
                        return 0;
                }

                return (totalQueueWaitTimeNanos.get() / 1_000_000.0)
                                / submitted;
        }

        /**
         * Returns the longest time any task has spent waiting in the queue.
         *
         * @return longest queue wait time in milliseconds
         */
        public double longestQueueWaitMillis() {

                return longestQueueWaitTimeNanos.get()
                                / 1_000_000.0;
        }

        /**
         * Returns the average throughput since executor startup.
         *
         * @return completed tasks per second
         */
        public double throughputTasksPerSecond() {

                long elapsed = System.nanoTime() - executorStartTime;

                if (elapsed <= 0) {
                        return 0;
                }

                return completedTasks()
                                / (elapsed / 1_000_000_000.0);
        }

        /**
         * Returns the percentage of successfully completed tasks.
         *
         * @return success rate percentage
         */
        public double successRate() {

                long submitted = submittedTasks();

                if (submitted == 0) {
                        return 100;
                }

                return (completedTasks() * 100.0)
                                / submitted;
        }

        /**
         * Returns the uptime of the executor in seconds.
         *
         * @return uptime in seconds
         */
        public long uptimeSeconds() {

                return (System.nanoTime() - executorStartTime)
                                / 1_000_000_000;
        }

        /**
         * Estimates worker utilization over the executor lifetime.
         *
         * @return utilization percentage
         */
        public double workerUtilizationPercent() {

                long uptimeNanos = System.nanoTime() - executorStartTime;

                long peakWorkers = Math.max(
                                1,
                                this.peakWorkers());

                double theoreticalCapacity = uptimeNanos * peakWorkers;

                if (theoreticalCapacity <= 0) {
                        return 0;
                }

                return (totalWorkerBusyTimeNanos.get()
                                * 100.0)
                                / theoreticalCapacity;
        }

        /*
         * ===== Updates =====
         */

        /**
         * Increments the count of submitted tasks by one.
         */
        public void incrementSubmitted() {
                submittedTasks.incrementAndGet();
        }

        /**
         * Increments the count of completed tasks by one.
         */
        public void incrementCompleted() {
                completedTasks.incrementAndGet();
        }

        /**
         * Increments the count of failed tasks by one.
         */
        public void incrementFailed() {
                failedTasks.incrementAndGet();
        }

        /**
         * Increments the count of rejected tasks by one.
         */
        public void incrementRejected() {
                rejectedTasks.incrementAndGet();
        }

        /**
         * Should be called when a worker thread starts executing a task.
         * Increments active worker count and updates peak if necessary.
         */
        public void workerStarted() {

                workersCreated.incrementAndGet();

                int current = currentWorkers.incrementAndGet();

                peakWorkers.updateAndGet(
                                peak -> Math.max(
                                                peak,
                                                current));
        }

        /**
         * Should be called when a worker thread finishes executing a task and becomes
         * idle.
         * Decrements active worker count and current worker count.
         */
        public void workerStopped() {

                workersDestroyed.incrementAndGet();

                currentWorkers.decrementAndGet();
        }

        /**
         * Should be called when a worker thread starts executing a task.
         * Increments active worker count.
         */
        public void workerBusy() {
                activeWorkers.incrementAndGet();
        }

        /**
         * Should be called when a worker thread finishes executing a task and becomes
         * idle.
         * Decrements active worker count.
         */
        public void workerIdle() {
                activeWorkers.decrementAndGet();
        }

        /**
         * Should be called when the executor scales up by adding a new worker.
         * Increments the scale-up event count.
         */
        public void scaleUp() {
                scaleUpEvents.incrementAndGet();
        }

        /**
         * Should be called when the executor scales down by removing a worker.
         * Increments the scale-down event count.
         */
        public void scaleDown() {
                scaleDownEvents.incrementAndGet();
        }

        /**
         * Updates the current queue size and checks if it exceeds the previous peak.
         *
         * @param queueSize current number of tasks in the queue
         */
        public void updateQueueSize(
                        int queueSize) {

                currentQueueSize.set(queueSize);

                peakQueueSize.updateAndGet(
                                peak -> Math.max(
                                                peak,
                                                queueSize));
        }

        /**
         * Should be called when a task completes execution to record its execution
         * time.
         *
         * @param executionTimeNanos execution time of the task in nanoseconds
         */
        public void recordExecutionTime(
                        long executionTimeNanos) {

                totalExecutionTimeNanos.addAndGet(
                                executionTimeNanos);

                totalWorkerBusyTimeNanos.addAndGet(
                                executionTimeNanos);

                longestExecutionTimeNanos.updateAndGet(
                                longest -> Math.max(
                                                longest,
                                                executionTimeNanos));
        }

        /**
         * Should be called when a task is dequeued for execution to record its wait
         * time
         * in the queue.
         *
         * @param waitTimeNanos time the task spent waiting in the queue in nanoseconds
         */
        public void recordQueueWaitTime(
                        long waitTimeNanos) {

                totalQueueWaitTimeNanos.addAndGet(
                                waitTimeNanos);

                longestQueueWaitTimeNanos.updateAndGet(
                                longest -> Math.max(
                                                longest,
                                                waitTimeNanos));
        }
}