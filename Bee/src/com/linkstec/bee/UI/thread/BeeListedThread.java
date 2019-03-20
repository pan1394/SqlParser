package com.linkstec.bee.UI.thread;

import java.util.ArrayList;
import java.util.List;

public class BeeListedThread extends Thread {
	private static List<BeeListedThread> list = new ArrayList<BeeListedThread>();
	private boolean started = false;

	public BeeListedThread() {
		makeList();
	}

	public BeeListedThread(Runnable target, String name) {
		super(target, name);
		makeList();
	}

	public BeeListedThread(Runnable target) {
		super(target);
		makeList();
	}

	public BeeListedThread(String name) {
		super(name);
		makeList();
	}

	public BeeListedThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
		makeList();
	}

	public BeeListedThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		makeList();
	}

	public BeeListedThread(ThreadGroup group, Runnable target) {
		super(group, target);
		makeList();
	}

	public BeeListedThread(ThreadGroup group, String name) {
		super(group, name);
		makeList();
	}

	@Override
	public synchronized void start() {
		super.start();
		started = true;
	}

	private void makeList() {
		list.add(this);
		manage();
	}

	private static Thread manager = null;

	private void manage() {
		if (manager == null || !manager.isAlive()) {
			manager = new Thread(new Runnable() {

				@Override
				public void run() {

					while (true) {
						int size = list.size();
						if (size == 0) {
							break;
						}
						List<BeeListedThread> removes = new ArrayList<BeeListedThread>();
						for (int i = 0; i < size; i++) {
							if (i < list.size()) {
								BeeListedThread t = list.get(i);
								if (!t.isAlive() && t.started) {
									removes.add(t);
								}
							}
						}
						for (BeeListedThread t : removes) {
							list.remove(t);
						}
						for (int i = 0; i < size; i++) {
							if (i < list.size()) {
								BeeListedThread t = list.get(i);
								if (!t.started) {
									t.start();
									break;
								}
							}
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			});
		}
		if (!manager.isAlive()) {
			manager.start();
		}
	}

	private static void removeDeads() {

		List<BeeListedThread> removes = new ArrayList<BeeListedThread>();

		for (BeeListedThread t : list) {
			if (!t.isAlive() && t.started) {
				removes.add(t);
			}
		}
		for (BeeListedThread t : removes) {
			list.remove(t);
		}

	}

}
