package com.cmw.adaptive.worker;

import java.util.concurrent.atomic.AtomicBoolean;

public class WorkerHandle {

    private final Thread thread;
    private final AtomicBoolean running;

    public WorkerHandle(
            Thread thread,
            AtomicBoolean running) {

        this.thread = thread;
        this.running = running;
    }

    public void stop() {

        running.set(false);

        thread.interrupt();
    }

    public void join()
            throws InterruptedException {

        thread.join();
    }

    public boolean isAlive() {
        return thread.isAlive();
    }

    public String name() {
        return thread.getName();
    }
}