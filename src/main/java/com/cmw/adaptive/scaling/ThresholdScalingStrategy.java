package com.cmw.adaptive.scaling;

/**
 * A simple scaling strategy that adjusts the number of worker threads based on
 * the current queue size and CPU load.
 *
 * <p>
 * The ThresholdScalingStrategy increases the number of workers when the queue
 * size exceeds a certain threshold and decreases it when the CPU load is high.
 * This approach helps to balance responsiveness with resource utilization.
 * </p>
 */
public class ThresholdScalingStrategy implements ScalingStrategy {

        private static final double CPU_SATURATION_THRESHOLD = 0.85;
        private static final int QUEUE_PER_WORKER = 5;

        @Override
        public int desiredWorkers(ScalingContext ctx) {

                int desired = Math.max(
                                ctx.minWorkers(),
                                (ctx.queueSize() / QUEUE_PER_WORKER) + 1);

                desired = Math.min(desired, ctx.maxWorkers());

                // sob saturação, avança apenas metade do caminho
                if (ctx.cpuLoad() > CPU_SATURATION_THRESHOLD) {
                        int headroom = desired - ctx.currentWorkers();
                        if (headroom > 0) {
                                desired = ctx.currentWorkers() + Math.max(1, headroom / 2);
                        }
                }

                return desired;
        }
}