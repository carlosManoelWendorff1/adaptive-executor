# Changelog

All notable changes to this project will be documented in this file.

The format is based on Keep a Changelog
and this project adheres to Semantic Versioning.

---

## [1.0.0] - 2026-06-19

### Added

#### Core Executor

- AdaptiveExecutor implementation.
- Priority-based task scheduling.
- Configurable minimum and maximum worker counts.
- Dynamic worker lifecycle management.
- Graceful shutdown support.

#### Task System

- AdaptiveTask interface.
- TaskPriority support.
- Interruptibility support.
- TrackedTask wrapper for runtime tracking.

#### Queue

- Thread-safe priority queue.
- FIFO ordering for tasks with identical priority.
- PriorityTaskWrapper implementation.

#### Worker Pool

- Dedicated worker threads.
- Automatic task execution.
- Failure isolation per task.
- Runtime worker activity tracking.

#### Auto Scaling

- ScalingManager.
- Threshold-based scaling strategy.
- PID-based scaling strategy.
- Worker scale-up and scale-down support.
- Cooldown mechanism to prevent scaling oscillation.

#### Metrics

- Submitted task count.
- Completed task count.
- Failed task count.
- Active worker count.
- Current worker count.
- Peak worker count.
- Current queue size.
- Peak queue size.
- Throughput metrics.
- Success rate metrics.
- Execution time metrics.
- Queue wait time metrics.

#### Testing

- Executor execution tests.
- Priority ordering tests.
- Graceful shutdown tests.
- Multi-task execution tests.

### Performance

- Dynamic worker provisioning.
- Automatic workload adaptation.
- Reduced idle thread overhead.
- Queue-driven scaling decisions.

---

## Roadmap

### Planned for v1.1.0

- Worker utilization based scaling.
- Configurable scaling policies.
- Metrics snapshots.
- Task cancellation support.
- Task timeout support.
- Delayed task scheduling.

### Planned for v1.2.0

- Preemptive task scheduling.
- Interruptible task execution.
- Cooperative task suspension.
- Queue starvation prevention.

### Planned for v2.0.0

- Virtual Threads support.
- Distributed execution.
- Cluster mode.
- Remote worker nodes.
- Monitoring dashboard.
- Micrometer integration.
- Prometheus integration.
