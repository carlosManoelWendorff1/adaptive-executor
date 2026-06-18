// package com.cmw.adaptive.queue;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import org.junit.jupiter.api.Test;

// import com.cmw.adaptive.task.AdaptiveTask;
// import com.cmw.adaptive.task.enums.TaskPriority;

// class PriorityTaskQueueTest {

// @Test
// void shouldReturnHigherPriorityFirst()
// throws Exception {

// PriorityTaskQueue queue = new PriorityTaskQueue();

// queue.offer(
// new AdaptiveTask() {

// @Override
// public TaskPriority priority() {
// return TaskPriority.LOW;
// }

// @Override
// public void run() {
// }
// });

// queue.offer(
// new AdaptiveTask() {

// @Override
// public TaskPriority priority() {
// return TaskPriority.HIGH;
// }

// @Override
// public void run() {
// }
// });

// AdaptiveTask first = queue.take();
// AdaptiveTask second = queue.take();

// assertEquals(
// TaskPriority.HIGH,
// first.priority());

// assertEquals(
// TaskPriority.LOW,
// second.priority());
// }

// @Test
// void shouldPreserveFifoForSamePriority()
// throws Exception {

// PriorityTaskQueue queue = new PriorityTaskQueue();

// AdaptiveTask firstTask = new AdaptiveTask() {

// @Override
// public TaskPriority priority() {
// return TaskPriority.NORMAL;
// }

// @Override
// public void run() {
// }
// };

// AdaptiveTask secondTask = new AdaptiveTask() {

// @Override
// public TaskPriority priority() {
// return TaskPriority.NORMAL;
// }

// @Override
// public void run() {
// }
// };

// queue.offer(firstTask);
// queue.offer(secondTask);

// assertEquals(
// firstTask,
// queue.take());

// assertEquals(
// secondTask,
// queue.take());
// }
// }