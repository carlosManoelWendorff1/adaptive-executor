package com.cmw.adaptive.queue;

import java.util.concurrent.atomic.AtomicLong;

import com.cmw.adaptive.task.TrackedTask;

public class PriorityTaskWrapper
        implements Comparable<PriorityTaskWrapper> {

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private final TrackedTask trackedTask;
    private final long sequence;

    public PriorityTaskWrapper(
            TrackedTask trackedTask) {

        this.trackedTask = trackedTask;
        this.sequence = SEQUENCE.incrementAndGet();
    }

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