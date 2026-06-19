// package com.cmw.adaptive;

// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.TimeUnit;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.jupiter.api.Test;

// import com.cmw.adaptive.task.AdaptiveTask;
// import com.cmw.adaptive.task.enums.TaskPriority;

// class AdaptiveExecutorTest {

// @Test
// void shouldExecuteSubmittedTask()
// throws Exception {

// AdaptiveExecutor executor = new AdaptiveExecutor(2, 2);

// CountDownLatch latch = new CountDownLatch(1);

// executor.submit(
// new AdaptiveTask() {

// @Override
// public TaskPriority priority() {
// return TaskPriority.NORMAL;
// }

// @Override
// public void run() {
// latch.countDown();
// }
// });

// boolean executed = latch.await(
// 5,
// TimeUnit.SECONDS);

// executor.shutdown();

// assertTrue(executed);
// }

// @Test
// void shouldExecuteAllTasks()
// throws Exception {

// AdaptiveExecutor executor = new AdaptiveExecutor(4, 4);

// int taskCount = 100;

// CountDownLatch latch = new CountDownLatch(taskCount);

// for (int i = 0; i < taskCount; i++) {

// executor.submit(
// new AdaptiveTask() {

// @Override
// public TaskPriority priority() {
// return TaskPriority.NORMAL;
// }

// @Override
// public void run() {
// latch.countDown();
// }
// });
// }

// boolean completed = latch.await(
// 10,
// TimeUnit.SECONDS);

// executor.shutdown();

// assertTrue(completed);
// assertEquals(0, latch.getCount());
// }

// @Test
// void shouldShutdownGracefully()
// throws Exception {

// AdaptiveExecutor executor = new AdaptiveExecutor(2, 2);

// executor.shutdown();

// assertTrue(true);
// }
// }