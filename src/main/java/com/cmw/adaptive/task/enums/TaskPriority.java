package com.cmw.adaptive.task.enums;

/**
 * Defines the scheduling priority levels for tasks executed by the
 * AdaptiveExecutor.
 *
 * <p>
 * Tasks with higher priority values will be scheduled before those with lower
 * values.
 * The executor's task queue will use these priorities to determine execution
 * order.
 * </p>
 */
public enum TaskPriority {

    /**
     * Indicates that a task has the highest scheduling priority and should be
     * executed before all other tasks.
     */
    HIGH(0),
    /**
     * Indicates that a task has normal scheduling priority and will be executed
     * after all HIGH priority tasks but before LOW priority tasks.
     */
    NORMAL(1),
    /**
     * Indicates that a task has the lowest scheduling priority and will be
     * executed after all HIGH and NORMAL priority tasks.
     */
    LOW(2);

    private final int value;

    /**
     * Creates a new TaskPriority with the given integer value.
     *
     * @param value integer value representing the priority level
     */
    TaskPriority(int value) {
        this.value = value;
    }

    /**
     * Returns the integer value associated with this priority level.
     *
     * @return integer priority value
     */
    public int value() {
        return value;
    }
}