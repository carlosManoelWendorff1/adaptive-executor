package com.cmw.adaptive.task.enums;

/**
 * Defines the interruption policy for tasks executed by the AdaptiveExecutor.
 *
 * <p>
 * Tasks marked as INTERRUPTIBLE may be interrupted if the executor needs
 * to scale down workers or shut down, while NON_INTERRUPTIBLE tasks will
 * be allowed to complete before termination.
 * </p>
 */
public enum Interruptibility {
    /**
     * Indicates that a task can be safely interrupted if the executor needs
     * to scale down workers or shut down.
     */
    INTERRUPTIBLE,
    /**
     * Indicates that a task should not be interrupted and will be allowed
     * to complete before the executor terminates.
     */
    NON_INTERRUPTIBLE
}