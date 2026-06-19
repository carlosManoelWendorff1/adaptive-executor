package com.cmw.adaptive.worker;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle for managing the lifecycle of a worker thread in the AdaptiveExecutor.
 *
 * <p>
 * The WorkerHandle encapsulates the worker thread and provides methods to
 * stop and join the thread, as well as check its status. It uses an
 * AtomicBoolean to signal the worker thread to stop gracefully.
 * </p>
 */
public class WorkerHandle {

    private final Thread thread;
    private final AtomicBoolean running;

    /**
     * Creates a new WorkerHandle for the given thread and running flag.
     *
     * @param thread  the worker thread to manage
     * @param running atomic boolean flag used to signal the worker to stop
     */
    public WorkerHandle(
            Thread thread,
            AtomicBoolean running) {

        this.thread = thread;
        this.running = running;
    }

    /**
     * Signals the worker thread to stop and interrupts it if it's waiting.
     */
    public void stop() {

        running.set(false);

        thread.interrupt();
    }

    /**
     * Waits for the worker thread to finish execution.
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public void join()
            throws InterruptedException {

        thread.join();
    }

    /**
     * Checks if the worker thread is still alive.
     *
     * @return true if the worker thread is alive, false otherwise
     */
    public boolean isAlive() {
        return thread.isAlive();
    }

    /**
     * Returns the name of the worker thread.
     * 
     * @return the name of the worker thread
     */
    public String name() {
        return thread.getName();
    }
}