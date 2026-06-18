package com.cmw.adaptive.scaling;

public class PidScalingStrategy
        implements ScalingStrategy {

    private final double kp;
    private final double ki;
    private final double kd;

    private double integral;
    private double previousError;

    public PidScalingStrategy(
            double kp,
            double ki,
            double kd) {

        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    @Override
    public int desiredWorkers(
            int queueSize,
            int currentWorkers,
            int minWorkers,
            int maxWorkers) {

        double error = queueSize;

        integral += error;

        double derivative = error - previousError;

        previousError = error;

        double output = (kp * error)
                + (ki * integral)
                + (kd * derivative);

        int desiredWorkers = (int) Math.round(output);

        desiredWorkers = Math.max(
                minWorkers,
                desiredWorkers);

        desiredWorkers = Math.min(
                maxWorkers,
                desiredWorkers);

        return desiredWorkers;
    }
}