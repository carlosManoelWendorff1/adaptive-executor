package com.cmw.adaptive.scaling;

import java.util.concurrent.atomic.AtomicBoolean;

import com.cmw.adaptive.AdaptiveExecutor;

public class ScalingManager implements Runnable {

    private static final long CHECK_INTERVAL_MS = 1000;

    private static final long COOLDOWN_MS = 5000;

    private final AdaptiveExecutor executor;

    private final AtomicBoolean running;

    private final ScalingStrategy strategy;

    private long lastScalingAction;

    public ScalingManager(
            AdaptiveExecutor executor,
            AtomicBoolean running,
            ScalingStrategy strategy) {

        this.executor = executor;
        this.running = running;
        this.strategy = strategy;
    }

    @Override
    public void run() {

        while (running.get()) {

            try {

                evaluate();

                Thread.sleep(
                        CHECK_INTERVAL_MS);

            } catch (InterruptedException e) {

                Thread.currentThread()
                        .interrupt();

                break;
            }
        }
    }

    private void evaluate() {

        if (cooldownActive()) {
            return;
        }

        int queueSize = executor.queueSize();

        int currentWorkers = executor.workerCount();

        int desiredWorkers = strategy.desiredWorkers(
                queueSize,
                currentWorkers,
                executor.minWorkers(),
                executor.maxWorkers());

        if (desiredWorkers > currentWorkers) {

            int amount = desiredWorkers
                    - currentWorkers;

            for (int i = 0; i < amount; i++) {
                executor.createWorker();
            }

            lastScalingAction = System.currentTimeMillis();

            System.out.printf(
                    "[SCALER] Scale UP -> workers=%d queue=%d desired=%d%n",
                    executor.workerCount(),
                    queueSize,
                    desiredWorkers);

            return;
        }

        if (desiredWorkers < currentWorkers) {

            int amount = currentWorkers
                    - desiredWorkers;

            for (int i = 0; i < amount; i++) {
                executor.removeWorker();
            }

            lastScalingAction = System.currentTimeMillis();

            System.out.printf(
                    "[SCALER] Scale DOWN -> workers=%d queue=%d desired=%d%n",
                    executor.workerCount(),
                    queueSize,
                    desiredWorkers);
        }
    }

    private boolean cooldownActive() {

        return System.currentTimeMillis()
                - lastScalingAction < COOLDOWN_MS;
    }
}