package com.cmw.adaptive.task;

/**
 * Encapsulates execution metadata for a task, including timestamps for
 * submission, start, and completion.
 *
 * <p>
 * Used internally by the executor to track task lifecycle and calculate
 * metrics such as queue wait time and execution duration.
 * </p>
 */
public final class TaskContext {

    private final long submittedAtNanos;

    private volatile long startedAtNanos;

    private volatile long completedAtNanos;

    /**
     * Creates a new TaskContext with the submission timestamp set to the current
     * time.
     */
    public TaskContext() {
        this.submittedAtNanos = System.nanoTime();
    }

    /**
     * Returns the timestamp when the task was submitted to the executor.
     *
     * @return submission timestamp in nanoseconds
     */
    public long submittedAtNanos() {
        return submittedAtNanos;
    }

    /**
     * Returns the timestamp when the task was marked as started.
     *
     * @return start timestamp in nanoseconds, or 0 if not started
     */
    public long startedAtNanos() {
        return startedAtNanos;
    }

    /**
     * Returns the timestamp when the task was marked as completed.
     *
     * @return completion timestamp in nanoseconds, or 0 if not completed
     */
    public long completedAtNanos() {
        return completedAtNanos;
    }

    /**
     * Marks the task as started by recording the start timestamp.
     */
    public void markStarted() {
        startedAtNanos = System.nanoTime();
    }

    /**
     * Marks the task as completed by recording the completion timestamp.
     */
    public void markCompleted() {
        completedAtNanos = System.nanoTime();
    }

    /**
     * Time spent waiting in the queue before execution started.
     *
     * @return queue wait time in nanoseconds, or 0 if not started
     */
    public long queueWaitTimeNanos() {

        if (startedAtNanos == 0) {
            return 0;
        }

        return startedAtNanos - submittedAtNanos;
    }

    /**
     * Time taken to execute the task.
     *
     * @return execution time in nanoseconds, or 0 if not completed
     */
    public long executionTimeNanos() {

        if (completedAtNanos == 0
                || startedAtNanos == 0) {

            return 0;
        }

        return completedAtNanos - startedAtNanos;
    }

    /**
     * Total time from submission to completion.
     *
     * @return total latency in nanoseconds, or 0 if not completed
     */
    public long totalLatencyNanos() {

        if (completedAtNanos == 0) {
            return 0;
        }

        return completedAtNanos - submittedAtNanos;
    }
}