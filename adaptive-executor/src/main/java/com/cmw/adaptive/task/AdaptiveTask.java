package com.cmw.adaptive.task;

import com.cmw.adaptive.task.enums.Interruptibility;
import com.cmw.adaptive.task.enums.TaskPriority;

public interface AdaptiveTask extends Runnable {

    TaskPriority priority();

    default Interruptibility interruptibility() {
        return Interruptibility.NON_INTERRUPTIBLE;
    }
}