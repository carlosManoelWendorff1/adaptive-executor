# Adaptive Executor

A lightweight Java task execution framework with priority-based scheduling, dynamic worker scaling, and runtime metrics collection.

The executor automatically adjusts its worker count at runtime based on queue depth, CPU load, and throughput feedback — without requiring manual pool size tuning.

---

## Features

- **Priority scheduling** — `HIGH` tasks are always processed before `LOW` tasks
- **Dynamic worker scaling** — worker count adjusts automatically between configurable limits
- **PID-based scaling** — smooth, adaptive control via a PID controller
- **Threshold-based scaling** — simple queue-depth scaling for predictable workloads
- **CPU-aware scaling** — moderates scale-up under CPU saturation instead of blocking it
- **Gradual scale-down** — removes workers incrementally to avoid sudden capacity drops
- **Lock-free metrics** — all counters use `AtomicLong` / `AtomicInteger`
- **Queue wait tracking** — records how long tasks waited before execution
- **Pluggable strategies** — implement `ScalingStrategy` to use any custom algorithm

---

## Architecture

```
                        ┌─────────────────────┐
                        │   AdaptiveExecutor   │
                        └──────────┬──────────┘
                                   │
              ┌────────────────────┼────────────────────┐
              │                    │                     │
   ┌──────────▼──────────┐  ┌─────▼──────┐  ┌─────────▼────────┐
   │  PriorityTaskQueue  │  │   Worker   │  │  ScalingManager  │
   │  (HIGH before LOW)  │  │  (×N)      │  │  (every 200ms)   │
   └─────────────────────┘  └─────┬──────┘  └─────────┬────────┘
                                   │                    │
                        ┌──────────▼──────────┐        │
                        │   ExecutorMetrics   │◄───────┘
                        │  (AtomicLong/Int)   │
                        └─────────────────────┘
```

### Components

| Component                  | Description                                                      |
| -------------------------- | ---------------------------------------------------------------- |
| `AdaptiveExecutor`         | Entry point — manages workers, queue, and scaling thread         |
| `PriorityTaskQueue`        | `PriorityBlockingQueue` wrapper ordered by `TaskPriority`        |
| `Worker`                   | Runnable that drains the queue and records execution metrics     |
| `ScalingManager`           | Background thread that evaluates scaling decisions every 200ms   |
| `PidScalingStrategy`       | PID controller computing desired worker count from queue size    |
| `ThresholdScalingStrategy` | Simple rule-based scaling (`queueSize / 5 + 1`)                  |
| `ScalingContext`           | Immutable snapshot of runtime state passed to the strategy       |
| `ExecutorMetrics`          | Lock-free counters for tasks, workers, queue, and timing         |
| `AdaptiveTask`             | Abstract task with `priority()` and `run()`                      |
| `TrackedTask`              | Wraps `AdaptiveTask` with queue entry timestamp for wait metrics |
| `TaskContext`              | Contextual metadata attached to each task submission             |
| `TaskPriority`             | `HIGH` / `LOW` priority levels                                   |

---

## Installation

```xml
<dependency>
    <groupId>com.cmw</groupId>
    <artifactId>adaptive-executor</artifactId>
    <version>1.0.0</version>
</dependency>
```

Build and install locally:

```bash
mvn clean install
```

---

## Quick Start

```java
AdaptiveExecutor executor = new AdaptiveExecutor(
        2,                          // minWorkers
        8,                          // maxWorkers
        new PidScalingStrategy(
                0.5,                // Kp
                0.01,               // Ki
                0.1));              // Kd

executor.submit(new TrackedTask(
        new AdaptiveTask() {

            @Override
            public TaskPriority priority() {
                return TaskPriority.HIGH;
            }

            @Override
            public void run() {
                System.out.println("Running task");
            }
        },
        new TaskContext()));

// wait for completion
while (executor.metrics().completedTasks() < expectedCount) {
    Thread.sleep(10);
}

executor.shutdown();
```

---

## Creating Tasks

Extend `AdaptiveTask` and implement `priority()` and `run()`:

```java
AdaptiveTask task = new AdaptiveTask() {

    @Override
    public TaskPriority priority() {
        return TaskPriority.HIGH;
    }

    @Override
    public void run() {
        // task logic here
    }
};
```

Wrap it in a `TrackedTask` before submitting:

```java
executor.submit(new TrackedTask(task, new TaskContext()));
```

---

## Task Priorities

| Priority            | Description                                                            |
| ------------------- | ---------------------------------------------------------------------- |
| `TaskPriority.HIGH` | Processed before any `LOW` task — use for latency-sensitive work       |
| `TaskPriority.LOW`  | Processed after all `HIGH` tasks are drained — use for background work |

---

## Scaling Strategies

### PidScalingStrategy

Uses a PID controller to compute desired worker count from queue depth. Suitable for workloads with variable or bursty demand.

```java
new PidScalingStrategy(
        0.5,   // Kp — reacts to current queue size
        0.01,  // Ki — reacts to accumulated backlog
        0.1)   // Kd — reacts to rate of queue change
```

Includes **anti-windup** to prevent integral overshoot beyond `maxWorkers`.

Under CPU saturation (load > 85%), scale-up advances at half the normal rate — preserving throughput under noisy neighbour conditions without blocking scaling entirely.

### ThresholdScalingStrategy

Simple rule: `desired = max(minWorkers, queueSize / 5 + 1)`. Suitable for predictable, low-variance workloads.

```java
new ThresholdScalingStrategy()
```

Also moderates scale-up under CPU saturation.

### Custom Strategy

Implement `ScalingStrategy` to use any custom algorithm:

```java
public class MyStrategy implements ScalingStrategy {

    @Override
    public int desiredWorkers(ScalingContext ctx) {
        return Math.min(ctx.queueSize(), ctx.maxWorkers());
    }
}
```

---

## ScalingContext

Passed to the scaling strategy on every evaluation cycle:

| Field                         | Type     | Description                                         |
| ----------------------------- | -------- | --------------------------------------------------- |
| `queueSize`                   | `int`    | Tasks waiting in the queue                          |
| `currentWorkers`              | `int`    | Total active worker threads                         |
| `activeWorkers`               | `int`    | Workers currently executing a task                  |
| `minWorkers`                  | `int`    | Worker count floor                                  |
| `maxWorkers`                  | `int`    | Worker count ceiling                                |
| `cpuLoad`                     | `double` | JVM process CPU load `[0.0, 1.0]`                   |
| `throughputPerWorker`         | `double` | Completed tasks per active worker in the last cycle |
| `previousThroughputPerWorker` | `double` | Same metric from the previous cycle                 |
| `avgExecutionTimeMillis`      | `double` | Rolling average task execution time                 |

---

## Scaling Behaviour

The `ScalingManager` evaluates every **200ms** with a **1s cooldown** between actions.

**Scale-up:** triggered when `desired > current`. Under CPU saturation, only half the gap is covered per cycle.

**Scale-down:** triggered when `desired < current`, subject to two guards:

1. Suppressed if `queueSize + activeWorkers >= currentWorkers` — all workers have pending work
2. Maximum **2 workers removed per cycle** — prevents sudden capacity collapse between bursts

---

## Metrics

Access runtime metrics at any time:

```java
ExecutorMetrics m = executor.metrics();
```

### Task Metrics

| Method             | Description                             |
| ------------------ | --------------------------------------- |
| `submittedTasks()` | Total tasks submitted                   |
| `completedTasks()` | Total tasks completed successfully      |
| `failedTasks()`    | Total tasks that threw an exception     |
| `successRate()`    | `completedTasks / submittedTasks * 100` |

### Worker Metrics

| Method             | Description                 |
| ------------------ | --------------------------- |
| `activeWorkers()`  | Workers currently executing |
| `currentWorkers()` | Total live worker threads   |
| `peakWorkers()`    | Maximum workers ever active |

### Queue Metrics

| Method                     | Description                             |
| -------------------------- | --------------------------------------- |
| `currentQueueSize()`       | Tasks waiting in queue                  |
| `peakQueueSize()`          | Maximum queue depth observed            |
| `averageQueueWaitMillis()` | Mean time tasks waited before execution |
| `longestQueueWaitMillis()` | Longest queue wait ever recorded        |

### Execution Metrics

| Method                         | Description                      |
| ------------------------------ | -------------------------------- |
| `averageExecutionTimeMillis()` | Mean task execution time         |
| `longestExecutionTimeMillis()` | Slowest task ever recorded       |
| `totalExecutionTimeMillis()`   | Sum of all execution times       |
| `throughputTasksPerSecond()`   | Completed tasks / uptime         |
| `workerUtilizationPercent()`   | Busy time / theoretical capacity |
| `uptimeSeconds()`              | Executor uptime                  |

### Example output

```text
Submitted Tasks      : 30
Completed Tasks      : 30
Failed Tasks         : 0
Success Rate         : 100.00%
Throughput           : 42.15 tasks/s
Active Workers       : 6
Current Workers      : 8
Peak Workers         : 8
Queue Size           : 0
Peak Queue Size      : 28
Average Queue Wait   : 12.45 ms
Average Exec Time    : 503.18 ms
```

---

## Requirements

- Java 21+
- Maven 3.6+

---

## Roadmap

### Version 1.x

- [x] Priority scheduling (`HIGH` / `LOW`)
- [x] Dynamic worker scaling
- [x] Threshold scaling strategy
- [x] PID scaling strategy
- [x] CPU-aware scaling moderation
- [x] Gradual scale-down
- [x] Runtime metrics
- [x] Queue wait time tracking

### Version 2.x

- [ ] Task cancellation
- [ ] Task timeout support
- [ ] Futures API
- [ ] Virtual thread support
- [ ] Prometheus / Micrometer integration

### Version 3.x

- [ ] Distributed execution
- [ ] Cluster mode
- [ ] Work stealing

---
## Benchmarks

Benchmarks were made using the [ Adaptive executor benchmark](https://github.com/carlosManoelWendorff1/adaptive-executor-benchmark) project in GITHUB.

---
## License

MIT License

Copyright (c) 2026 Carlos Manoel Wendorff
