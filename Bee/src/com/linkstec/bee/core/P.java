package com.linkstec.bee.core;

import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.fw.action.BProcessIF;

public class P implements BProcessIF {
	public static void check(Object node) {
		Thread thread = Thread.currentThread();
		if (thread instanceof BeeThread) {
			BeeThread bee = (BeeThread) thread;
			bee.checkStatus();
		}
	}

	public static void start(int steps) {
		Thread thread = Thread.currentThread();
		if (thread instanceof BeeThread) {
			BeeThread bee = (BeeThread) thread;
			bee.startTask(steps);
		}
	}

	public static void go() {
		Thread thread = Thread.currentThread();
		if (thread instanceof BeeThread) {
			BeeThread bee = (BeeThread) thread;
			bee.taskNext();
		}
	}

	public static void end() {
		Thread thread = Thread.currentThread();
		if (thread instanceof BeeThread) {
			BeeThread bee = (BeeThread) thread;
			bee.endTask();
		}
	}

	@Override
	public void checkB(Object node) {
		P.check(node);

	}

	@Override
	public void startB(int steps) {
		P.start(steps);

	}

	@Override
	public void goB() {
		P.go();

	}

	@Override
	public void endB() {
		// TODO Auto-generated method stub

	}
}
