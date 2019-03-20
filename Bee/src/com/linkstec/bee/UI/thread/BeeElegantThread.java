package com.linkstec.bee.UI.thread;

import java.util.ArrayList;
import java.util.List;

public class BeeElegantThread extends Thread {
	private static List<Thread> threads = new ArrayList<Thread>();

	public static void invokerLater(Call call) {

	}

	private static class BeeRunnable implements Runnable {
		private Call call;
		private boolean running = false;
		private boolean end = false;

		@Override
		public void run() {
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			running = true;
			call.run();
			end = true;

		}

	}

	public abstract static class Call {
		public abstract void run();
	}
}
