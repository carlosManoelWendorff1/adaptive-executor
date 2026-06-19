package com.cmw.adaptive.worker;

/**
 * Represents a task that has been wrapped with execution metadata for tracking
 * purposes within the AdaptiveExecutor.
 *
 * <p>
 * The TrackedTask class encapsulates an AdaptiveTask along with a TaskContext
 * that collects runtime metrics such as submission time, start time, and
 * completion time. This allows the executor to monitor task performance and
 * make informed scaling decisions based on collected data.
 * </p>
 */
public class TrackedTask {

}
