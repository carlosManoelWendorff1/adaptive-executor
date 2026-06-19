package com.cmw.adaptive.scaling;

/**
 * Scaling strategy based on a PID controller.
 *
 * <p>
 * Worker count is adjusted according to queue pressure,
 * throughput trends and system load.
 * </p>
 *
 * <p>
 * PID terms:
 * </p>
 * <ul>
 * <li>Kp - proportional gain</li>
 * <li>Ki - integral gain</li>
 * <li>Kd - derivative gain</li>
 * </ul>
 */
public class PidScalingStrategy
                implements ScalingStrategy {

        private static final double CPU_SATURATION_THRESHOLD = 0.85;

        private final double kp;
        private final double ki;
        private final double kd;

        private double integral;
        private double previousError;

        /**
         * Creates a PID scaling strategy.
         *
         * @param kp proportional gain
         * @param ki integral gain
         * @param kd derivative gain
         */
        public PidScalingStrategy(
                        double kp,
                        double ki,
                        double kd) {
                this.kp = kp;
                this.ki = ki;
                this.kd = kd;
        }

        /**
         * Calculates the desired worker count using PID control.
         *
         * @param ctx current scaling context
         *
         * @return desired worker count
         */
        @Override
        public int desiredWorkers(
                        ScalingContext ctx) {

                double error = ctx.queueSize();

                double tentativeIntegral = integral + error;
                double tentativeOutput = (kp * error)
                                + (ki * tentativeIntegral)
                                + (kd * (error - previousError));

                if (tentativeOutput <= ctx.maxWorkers()) {
                        integral = tentativeIntegral;
                }

                double derivative = error - previousError;
                previousError = error;

                double output = (kp * error)
                                + (ki * integral)
                                + (kd * derivative);

                int desired = (int) Math.round(output);

                // sob saturação de CPU, escala de forma moderada em vez de bloquear
                if (ctx.cpuLoad() > CPU_SATURATION_THRESHOLD) {
                        int headroom = desired - ctx.currentWorkers();
                        if (headroom > 0) {
                                // avança apenas metade do caminho até o desejado
                                desired = ctx.currentWorkers() + Math.max(1, headroom / 2);
                        }
                }

                // se throughput por worker caiu após scale up, não escala mais
                if (ctx.throughputPerWorker() < ctx.previousThroughputPerWorker()
                                && desired > ctx.currentWorkers()) {
                        desired = ctx.currentWorkers();
                }

                desired = Math.max(ctx.minWorkers(), desired);
                desired = Math.min(ctx.maxWorkers(), desired);

                return desired;
        }
}