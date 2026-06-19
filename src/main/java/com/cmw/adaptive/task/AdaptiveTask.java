package com.cmw.adaptive.task;

import com.cmw.adaptive.task.enums.Interruptibility;
import com.cmw.adaptive.task.enums.TaskPriority;

/**
 * Represents a unit of work executable by the AdaptiveExecutor.
 *
 * <p>
 * Tasks provide priority information used by the scheduler
 * and may optionally define interruption behavior.
 * </p>
 */
public interface AdaptiveTask extends Runnable {

    /**
     * Returns the scheduling priority of this task.
     *
     * @return task priority
     */
    TaskPriority priority();

    /**
     * Returns the interruption policy for this task.
     *
     * @return interruption behavior
     */
    default Interruptibility interruptibility() {
        return Interruptibility.NON_INTERRUPTIBLE;
    }
}