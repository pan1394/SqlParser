package com.linkstec.bee.UI.thread;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.UI.progress.IBeeProgress;
import com.linkstec.bee.core.Application;

public class BeeThread extends Thread {
	private Object userObject;
	public static final int STATUS_TOP = 1;
	public static final int STATUS_SUSPEND = 2;
	public static final int STATUS_RESUME = 3;
	private int status = 0;
	private IBeeProgress bar;
	private BeeTask currentTask = null;
	private BeeTask topTask = null;
	private Hashtable<Object, Object> userAttributes = new Hashtable<Object, Object>();

	private List<BeeThread> childs = new ArrayList<BeeThread>();

	public BeeThread() {
		initialize();
	}

	public BeeThread(Runnable target) {
		super(target);
		initialize();
	}

	public BeeThread(String name) {
		super(name);
		initialize();
	}

	public BeeThread(ThreadGroup group, Runnable target) {
		super(group, target);
		initialize();
	}

	public BeeThread(ThreadGroup group, String name) {
		super(group, name);
		initialize();
	}

	public BeeThread(Runnable target, String name) {
		super(target, name);
		initialize();
	}

	public BeeThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		initialize();

	}

	public BeeThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
		initialize();
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public int getStatus() {
		return status;
	}

	public void addChild(BeeThread thread) {
		this.childs.add(thread);
	}

	public void initialize() {
		Thread thread = Thread.currentThread();
		if (thread instanceof BeeThread) {
			BeeThread bee = (BeeThread) thread;
			List<BeeThread> list = bee.childs;

			List<BeeThread> removes = new ArrayList<BeeThread>();
			for (BeeThread t : list) {
				if (!t.isAlive()) {
					removes.add(t);

				}
			}
			for (BeeThread t : removes) {
				list.remove(t);
			}
			bee.addChild(this);
		}
	}

	public void setStatus(int status) {

		this.status = status;
		if (this.status == BeeThread.STATUS_RESUME) {
			this.waiting = false;
		}
		synchronized (this.childs) {
			List<BeeThread> list = this.childs;
			List<BeeThread> removes = new ArrayList<BeeThread>();
			for (BeeThread t : list) {
				if (!t.isAlive()) {
					removes.add(t);
				} else {
					t.setStatus(status);
				}
			}
			for (BeeThread t : removes) {
				list.remove(t);
			}
		}
	}

	public void checkStatus() {
		if (this.status == BeeThread.STATUS_SUSPEND) {
			waiting = true;
			this.doSuspend();
		} else if (this.status == BeeThread.STATUS_TOP) {
			this.doStop();
		}

	}

	public IBeeProgress getProgress() {
		return bar;
	}

	public void setProgress(IBeeProgress bar) {
		this.bar = bar;
	}

	private boolean waiting = false;

	private void doSuspend() {
		try {
			while (waiting) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void doStop() {
		Application.getInstance().getEditor().getStatusBar().setMessag("変換が中断されました");
		Application.getInstance().getEditor().getStatusBar().endProgress();
		throw new RuntimeException("STOP");
	}

	public boolean isToppedIntently(Exception e) {
		String method = e.getStackTrace()[0].getMethodName();
		String cls = e.getStackTrace()[0].getClassName();
		if (method.equals("doStop") && cls.equals(BeeThread.class.getName())) {
			Application.getInstance().getEditor().getStatusBar().setMessag("変換が中断されました");
			Application.getInstance().getEditor().getStatusBar().endProgress();
			return true;
		}
		return false;
	}

	public void startTask(int step) {
		if (this.bar == null) {
			return;
		}
		if (this.currentTask == null) {
			this.currentTask = new BeeTask(step);
			this.topTask = this.currentTask;
			this.bar.setValue(0);
		} else {
			this.currentTask = this.currentTask.start(step);
			// this.bar.setValue(this.topTask.getProgress());
		}
	}

	public void taskNext() {
		if (this.bar == null) {
			return;
		}
		if (this.currentTask == null) {
			throw new RuntimeException("no task started");
		}
		this.currentTask.next();
		this.bar.setValue(this.topTask.getProgress());
	}

	public void endTask() {
		if (this.bar == null) {
			return;
		}
		if (this.currentTask == null) {
			throw new RuntimeException("no task started");
		}
		this.currentTask = this.currentTask.end();
		if (this.currentTask != null) {
			currentTask.clearChild();
		}
	}

	public void setUserAttributes(Hashtable<Object, Object> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public void addUserAttribute(Object key, Object value) {
		this.userAttributes.put(key, value);
	}

	public void removeUserAttribute(Object key) {
		this.userAttributes.remove(key);
	}

	public Object getUserAttribute(Object key) {
		if (this.userAttributes != null) {
			return this.userAttributes.get(key);
		} else {
			return null;
		}
	}

}
