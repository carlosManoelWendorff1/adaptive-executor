# Adaptive Executor

Adaptive Executor is a lightweight Java task execution framework featuring:

- Priority-based task scheduling
- Dynamic worker scaling
- Multiple scaling strategies
- Runtime metrics collection
- Queue wait time tracking
- Execution time tracking
- Throughput monitoring

The project was created as an experimental adaptive thread pool that can automatically adjust worker count according to workload.

---

# Features

## Task Priorities

Tasks can be submitted with different priorities:

```java
TaskPriority.HIGH
TaskPriority.NORMAL
TaskPriority.LOW
```

Higher-priority tasks are executed before lower-priority tasks.

---

## Dynamic Scaling

Workers are automatically scaled between configurable limits.

```java
AdaptiveExecutor executor =
        new AdaptiveExecutor(
                2,
                8,
                new ThresholdScalingStrategy());
```

Example:

- Minimum workers: 2
- Maximum workers: 8

---

## Scaling Strategies

### Threshold Strategy

Simple queue-based scaling.

```java
new ThresholdScalingStrategy()
```

### PID Strategy

Smooth adaptive scaling using a PID controller.

```java
new PidScalingStrategy(
        0.5,
        0.01,
        0.1)
```

Useful for workloads with frequent spikes.

---

## Metrics

Built-in runtime metrics:

### Task Metrics

- Submitted Tasks
- Completed Tasks
- Failed Tasks
- Success Rate

### Worker Metrics

- Active Workers
- Current Workers
- Peak Workers

### Queue Metrics

- Current Queue Size
- Peak Queue Size
- Average Queue Wait Time
- Longest Queue Wait Time

### Execution Metrics

- Average Execution Time
- Longest Execution Time
- Throughput (tasks/sec)

---

# Installation

## Maven

```xml
<dependency>
    <groupId>com.cmw</groupId>
    <artifactId>adaptive-executor</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

# Quick Start

```java
AdaptiveExecutor executor =
        new AdaptiveExecutor(
                2,
                8,
                new ThresholdScalingStrategy());

executor.submit(
        new TrackedTask(
                new AdaptiveTask() {

                    @Override
                    public TaskPriority priority() {
                        return TaskPriority.HIGH;
                    }

                    @Override
                    public void run() {
                        System.out.println("Running task");
                    }
                }));

executor.shutdown();
```

---

# Creating Tasks

Example:

```java
AdaptiveTask task =
        new AdaptiveTask() {

            @Override
            public TaskPriority priority() {
                return TaskPriority.HIGH;
            }

            @Override
            public void run() {
                System.out.println("Hello");
            }
        };
```

---

# Monitoring

Access metrics at runtime:

```java
ExecutorMetrics metrics =
        executor.metrics();

System.out.println(
        metrics.completedTasks());

System.out.println(
        metrics.averageExecutionTimeMillis());

System.out.println(
        metrics.throughputTasksPerSecond());
```

---

# Architecture

```text
                +------------------+
                | AdaptiveExecutor |
                +--------+---------+
                         |
                         v
                +------------------+
                | Priority Queue   |
                +--------+---------+
                         |
         +---------------+---------------+
         |                               |
         v                               v
 +---------------+              +---------------+
 | Worker Thread |              | Worker Thread |
 +---------------+              +---------------+
         ^
         |
 +---------------+
 | ScalingManager|
 +---------------+
         ^
         |
 +---------------+
 | ScalingStrategy
 +---------------+
```

---

# Roadmap

## Version 1.x

- [x] Priority scheduling
- [x] Dynamic worker scaling
- [x] Threshold scaling
- [x] PID scaling
- [x] Runtime metrics
- [x] Queue wait metrics

## Version 2.x

- [ ] Task preemption
- [ ] Task cancellation
- [ ] Task timeout support
- [ ] Scheduled tasks
- [ ] Futures API
- [ ] Virtual thread support
- [ ] Worker affinity
- [ ] Prometheus integration
- [ ] Micrometer integration

## Version 3.x

- [ ] Distributed execution
- [ ] Cluster mode
- [ ] Remote workers
- [ ] Work stealing

---

# Performance

Example run:

```text
Submitted: 30
Completed: 30
Failed: 0

Peak Workers: 8

Average Queue Wait:
12.45 ms

Average Execution Time:
503.18 ms

Throughput:
42.15 tasks/sec
```

---

# License

MIT License

Copyright (c) 2026 Carlos Manoel Wendorff
