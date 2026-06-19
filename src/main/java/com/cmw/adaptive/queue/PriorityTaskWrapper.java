package com.cmw.adaptive.queue;

import java.util.concurrent.atomic.AtomicLong;

import com.cmw.adaptive.task.TrackedTask;

/**
 * Wrapper for tasks that need to be prioritized in the priority queue.
 */
public class PriorityTaskWrapper
        implements Comparable<PriorityTaskWrapper> {

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private final TrackedTask trackedTask;
    private final long sequence;

    /**
     * Creates a new priority task wrapper for the given tracked task.
     *
     * <p>
     * The wrapper assigns a unique sequence number to each task to ensure
     * FIFO ordering among tasks with the same priority.
     * </p>
     *
     * @param trackedTask task to wrap
     */
    public PriorityTaskWrapper(
            TrackedTask trackedTask) {

        this.trackedTask = trackedTask;
        this.sequence = SEQUENCE.incrementAndGet();
    }

    /**
     * Returns the wrapped tracked task.
     *
     * @return tracked task instance
     */

    public TrackedTask trackedTask() {
        return trackedTask;
    }

    @Override
    public int compareTo(
            PriorityTaskWrapper other) {

        int priorityComparison = Integer.compare(
                trackedTask.task().priority().value(),
                other.trackedTask.task().priority().value());

        if (priorityComparison != 0) {
            return priorityComparison;
        }

        return Long.compare(
                sequence,
                other.sequence);
    }
}