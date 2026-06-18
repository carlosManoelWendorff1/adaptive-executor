package com.cmw.adaptive.task;

public final class TaskContext {

    private final long submittedAtNanos;

    private volatile long startedAtNanos;

    private volatile long completedAtNanos;

    public TaskContext() {
        this.submittedAtNanos = System.nanoTime();
    }

    public long submittedAtNanos() {
        return submittedAtNanos;
    }

    public long startedAtNanos() {
        return startedAtNanos;
    }

    public long completedAtNanos() {
        return completedAtNanos;
    }

    public void markStarted() {
        startedAtNanos = System.nanoTime();
    }

    public void markCompleted() {
        completedAtNanos = System.nanoTime();
    }

    public long queueWaitTimeNanos() {

        if (startedAtNanos == 0) {
            return 0;
        }

        return startedAtNanos - submittedAtNanos;
    }

    public long executionTimeNanos() {

        if (completedAtNanos == 0
                || startedAtNanos == 0) {

            return 0;
        }

        return completedAtNanos - startedAtNanos;
    }

    public long totalLatencyNanos() {

        if (completedAtNanos == 0) {
            return 0;
        }

        return completedAtNanos - submittedAtNanos;
    }
}