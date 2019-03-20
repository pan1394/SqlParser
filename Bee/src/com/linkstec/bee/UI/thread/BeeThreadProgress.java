package com.linkstec.bee.UI.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BeeThreadProgress {

	public static void check() {
		Thread thread = Thread.currentThread();
		if (thread instanceof BeeThread) {
			BeeThread bee = (BeeThread) thread;
			bee.checkStatus();

		}
	}

	public static void addTask() {
		Thread thread = Thread.currentThread();
		if (thread instanceof BeeThread) {
			BeeThread bee = (BeeThread) thread;
			bee.checkStatus();

		}

		ThreadLocal<Integer> local = ThreadLocal.withInitial(() -> 0);
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(10);
		RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
		ThreadPoolExecutor pool = new ThreadPoolExecutor(3, 3, 0, TimeUnit.SECONDS, queue, handler);
		// handler.rejectedExecution(thread, executor);
	}

}
