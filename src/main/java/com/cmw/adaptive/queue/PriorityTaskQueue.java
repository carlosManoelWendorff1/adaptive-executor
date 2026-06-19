package com.cmw.adaptive.queue;

import java.util.concurrent.PriorityBlockingQueue;

import com.cmw.adaptive.task.TrackedTask;

/**
 * Thread-safe priority queue for managing tasks in the AdaptiveExecutor.
 *
 * <p>
 * Tasks are wrapped in a {@link PriorityTaskWrapper} to associate them with
 * their scheduling priority.
 * The queue uses a {@link PriorityBlockingQueue} to ensure that higher
 * priority tasks are always executed before lower priority ones.
 * </p>
 */

public class PriorityTaskQueue {

    private final PriorityBlockingQueue<PriorityTaskWrapper> queue = new PriorityBlockingQueue<>();

    /**
     * Adds a task to the queue with its associated priority.
     *
     * @param task task to enqueue
     */
    public void offer(TrackedTask task) {

        queue.offer(
                new PriorityTaskWrapper(task));
    }

    /**
     * Retrieves and removes the highest priority task from the queue,
     * waiting if necessary until a task becomes available.
     *
     * @return the highest priority tracked task
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public TrackedTask take()
            throws InterruptedException {

        return queue.take().trackedTask();
    }

    /**
     * Returns the current number of tasks waiting in the queue.
     *
     * @return current queue size
     */
    public int size() {
        return queue.size();
    }
}