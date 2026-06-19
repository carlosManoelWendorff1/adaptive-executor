package com.cmw.adaptive.task;

/**
 * Wrapper around an {@link AdaptiveTask} that attaches
 * execution metadata through a {@link TaskContext}.
 *
 * <p>
 * Used internally by the executor to collect runtime metrics
 * such as queue wait time and execution duration.
 * </p>
 */
public final class TrackedTask {

    private final AdaptiveTask task;

    private final TaskContext context;

    /**
     * Creates a tracked task with a fresh task context.
     *
     * @param task task to wrap
     */
    public TrackedTask(
            AdaptiveTask task) {

        this.task = task;
        this.context = new TaskContext();
    }

    /**
     * Creates a tracked task with the given task context.
     *
     * @param task    task to wrap
     * @param context existing task context to use for tracking
     */
    public TrackedTask(
            AdaptiveTask task, TaskContext context) {

        this.task = task;
        this.context = context;
    }

    /**
     * Returns the wrapped task.
     *
     * @return task instance
     */
    public AdaptiveTask task() {
        return task;
    }

    /**
     * Returns the associated execution context.
     *
     * @return task context
     */
    public TaskContext context() {
        return context;
    }
}