package com.cmw.adaptive.task;

public final class TrackedTask {

    private final AdaptiveTask task;

    private final TaskContext context;

    public TrackedTask(
            AdaptiveTask task) {

        this.task = task;
        this.context = new TaskContext();
    }

    public TrackedTask(
            AdaptiveTask task, TaskContext context) {

        this.task = task;
        this.context = context;
    }

    public AdaptiveTask task() {
        return task;
    }

    public TaskContext context() {
        return context;
    }
}