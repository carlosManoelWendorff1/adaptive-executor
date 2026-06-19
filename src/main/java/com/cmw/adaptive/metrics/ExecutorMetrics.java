package com.cmw.adaptive.metrics;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

        public long submittedTasks() {
                return submittedTasks.get();
        }

        public long completedTasks() {
                return completedTasks.get();
        }

        public long failedTasks() {
                return failedTasks.get();
        }

        public long rejectedTasks() {
                return rejectedTasks.get();
        }

        /*
         * ===== Worker Getters =====
         */

        public int activeWorkers() {
                return activeWorkers.get();
        }

        public int currentWorkers() {
                return currentWorkers.get();
        }

        public int peakWorkers() {
                return peakWorkers.get();
        }

        public long workersCreated() {
                return workersCreated.get();
        }

        public long workersDestroyed() {
                return workersDestroyed.get();
        }

        /*
         * ===== Queue Getters =====
         */

        public int currentQueueSize() {
                return currentQueueSize.get();
        }

        public int peakQueueSize() {
                return peakQueueSize.get();
        }

        /*
         * ===== Scaling Getters =====
         */

        public long scaleUpEvents() {
                return scaleUpEvents.get();
        }

        public long scaleDownEvents() {
                return scaleDownEvents.get();
        }

        /*
         * ===== Derived Metrics =====
         */

        public double totalExecutionTimeMillis() {
                return totalExecutionTimeNanos.get() / 1_000_000.0;
        }

        public double totalQueueWaitTimeMillis() {
                return totalQueueWaitTimeNanos.get() / 1_000_000.0;
        }

        public double averageExecutionTimeMillis() {

                long completed = completedTasks();

                if (completed == 0) {
                        return 0;
                }

                return (totalExecutionTimeNanos.get() / 1_000_000.0)
                                / completed;
        }

        public double longestExecutionTimeMillis() {

                return longestExecutionTimeNanos.get()
                                / 1_000_000.0;
        }

        public double averageQueueWaitMillis() {

                long submitted = submittedTasks();

                if (submitted == 0) {
                        return 0;
                }

                return (totalQueueWaitTimeNanos.get() / 1_000_000.0)
                                / submitted;
        }

        public double longestQueueWaitMillis() {

                return longestQueueWaitTimeNanos.get()
                                / 1_000_000.0;
        }

        public double throughputTasksPerSecond() {

                long elapsed = System.nanoTime() - executorStartTime;

                if (elapsed <= 0) {
                        return 0;
                }

                return completedTasks()
                                / (elapsed / 1_000_000_000.0);
        }

        public double successRate() {

                long submitted = submittedTasks();

                if (submitted == 0) {
                        return 100;
                }

                return (completedTasks() * 100.0)
                                / submitted;
        }

        public long uptimeSeconds() {

                return (System.nanoTime() - executorStartTime)
                                / 1_000_000_000;
        }

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

        public void incrementSubmitted() {
                submittedTasks.incrementAndGet();
        }

        public void incrementCompleted() {
                completedTasks.incrementAndGet();
        }

        public void incrementFailed() {
                failedTasks.incrementAndGet();
        }

        public void incrementRejected() {
                rejectedTasks.incrementAndGet();
        }

        public void workerStarted() {

                workersCreated.incrementAndGet();

                int current = currentWorkers.incrementAndGet();

                peakWorkers.updateAndGet(
                                peak -> Math.max(
                                                peak,
                                                current));
        }

        public void workerStopped() {

                workersDestroyed.incrementAndGet();

                currentWorkers.decrementAndGet();
        }

        public void workerBusy() {
                activeWorkers.incrementAndGet();
        }

        public void workerIdle() {
                activeWorkers.decrementAndGet();
        }

        public void scaleUp() {
                scaleUpEvents.incrementAndGet();
        }

        public void scaleDown() {
                scaleDownEvents.incrementAndGet();
        }

        public void updateQueueSize(
                        int queueSize) {

                currentQueueSize.set(queueSize);

                peakQueueSize.updateAndGet(
                                peak -> Math.max(
                                                peak,
                                                queueSize));
        }

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