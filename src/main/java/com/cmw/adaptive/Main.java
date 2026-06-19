package com.cmw.adaptive;

import com.cmw.adaptive.scaling.PidScalingStrategy;
import com.cmw.adaptive.scaling.ThresholdScalingStrategy;
import com.cmw.adaptive.task.AdaptiveTask;
import com.cmw.adaptive.task.TaskContext;
import com.cmw.adaptive.task.TrackedTask;
import com.cmw.adaptive.task.enums.TaskPriority;

/**
 * Demonstrates the usage of the AdaptiveExecutor with a mix of high and low
 * priority tasks.
 *
 * <p>
 * Submits a series of tasks to the executor, periodically printing runtime
 * metrics until all tasks are completed.
 * </p>
 */
public class Main {

        /**
         * Entry point for the demonstration.
         *
         * @param args command-line arguments (not used)
         *
         * @throws Exception if any error occurs during execution
         */
        public static void main(String[] args)
                        throws Exception {

                boolean usePid = true;

                AdaptiveExecutor executor = new AdaptiveExecutor(
                                2,
                                8,
                                usePid
                                                ? new PidScalingStrategy(
                                                                0.5,
                                                                0.01,
                                                                0.1)
                                                : new ThresholdScalingStrategy());

                /*
                 * HIGH tasks
                 */
                for (int i = 0; i < 10; i++) {

                        int id = i;

                        executor.submit(
                                        new TrackedTask(
                                                        new AdaptiveTask() {

                                                                @Override
                                                                public TaskPriority priority() {
                                                                        return TaskPriority.HIGH;
                                                                }

                                                                @Override
                                                                public void run() {

                                                                        System.out.printf(
                                                                                        "[HIGH] Task %d running on %s%n",
                                                                                        id,
                                                                                        Thread.currentThread()
                                                                                                        .getName());

                                                                        try {
                                                                                Thread.sleep(1000);
                                                                        } catch (InterruptedException e) {
                                                                                Thread.currentThread()
                                                                                                .interrupt();
                                                                        }
                                                                }
                                                        },
                                                        new TaskContext()));
                }

                /*
                 * LOW tasks
                 */
                for (int i = 0; i < 20; i++) {

                        int id = i;

                        executor.submit(
                                        new TrackedTask(
                                                        new AdaptiveTask() {

                                                                @Override
                                                                public TaskPriority priority() {
                                                                        return TaskPriority.LOW;
                                                                }

                                                                @Override
                                                                public void run() {

                                                                        System.out.printf(
                                                                                        "[LOW] Task %d running on %s%n",
                                                                                        id,
                                                                                        Thread.currentThread()
                                                                                                        .getName());

                                                                        try {
                                                                                Thread.sleep(500);
                                                                        } catch (InterruptedException e) {
                                                                                Thread.currentThread()
                                                                                                .interrupt();
                                                                        }
                                                                }
                                                        },
                                                        new TaskContext()));
                }

                while (executor.metrics()
                                .completedTasks() < 30) {

                        Thread.sleep(1000);

                        printMetrics(executor);
                }

                executor.shutdown();

                System.out.println();
                System.out.println("=== FINAL METRICS ===");

                printMetrics(executor);
        }

        private static void printMetrics(
                        AdaptiveExecutor executor) {

                var metrics = executor.metrics();

                System.out.println();
                System.out.println("=== Runtime Metrics ===");

                System.out.println(
                                "Submitted Tasks      : "
                                                + metrics.submittedTasks());

                System.out.println(
                                "Completed Tasks      : "
                                                + metrics.completedTasks());

                System.out.println(
                                "Failed Tasks         : "
                                                + metrics.failedTasks());

                System.out.println(
                                "Success Rate         : "
                                                + String.format(
                                                                "%.2f%%",
                                                                metrics.successRate()));

                System.out.println(
                                "Throughput           : "
                                                + String.format(
                                                                "%.2f tasks/s",
                                                                metrics.throughputTasksPerSecond()));

                System.out.println(
                                "Active Workers       : "
                                                + metrics.activeWorkers());

                System.out.println(
                                "Current Workers      : "
                                                + metrics.currentWorkers());

                System.out.println(
                                "Peak Workers         : "
                                                + metrics.peakWorkers());

                System.out.println(
                                "Queue Size           : "
                                                + metrics.currentQueueSize());

                System.out.println(
                                "Peak Queue Size      : "
                                                + metrics.peakQueueSize());

                System.out.println(
                                "Average Queue Wait   : "
                                                + String.format(
                                                                "%.2f ms",
                                                                metrics.averageQueueWaitMillis()));

                System.out.println(
                                "Longest Queue Wait   : "
                                                + String.format(
                                                                "%.2f ms",
                                                                metrics.longestQueueWaitMillis()));

                System.out.println(
                                "Average Exec Time    : "
                                                + String.format(
                                                                "%.2f ms",
                                                                metrics.averageExecutionTimeMillis()));

                System.out.println(
                                "Longest Exec Time    : "
                                                + String.format(
                                                                "%.2f ms",
                                                                metrics.longestExecutionTimeMillis()));

                System.out.println(
                                "Total Exec Time      : "
                                                + String.format(
                                                                "%.2f ms",
                                                                metrics.totalExecutionTimeMillis()));

                System.out.println(
                                "Total Queue Wait     : "
                                                + String.format(
                                                                "%.2f ms",
                                                                metrics.totalQueueWaitTimeMillis()));
        }
}