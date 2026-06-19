package com.cmw.adaptive.queue;

import java.util.concurrent.PriorityBlockingQueue;

import com.cmw.adaptive.task.TrackedTask;

public class PriorityTaskQueue {

    private final PriorityBlockingQueue<PriorityTaskWrapper> queue = new PriorityBlockingQueue<>();

    public void offer(TrackedTask task) {

        queue.offer(
                new PriorityTaskWrapper(task));
    }

    public TrackedTask take()
            throws InterruptedException {

        return queue.take().trackedTask();
    }

    public int size() {
        return queue.size();
    }
}