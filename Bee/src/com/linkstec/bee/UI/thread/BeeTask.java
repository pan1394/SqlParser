package com.linkstec.bee.UI.thread;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.Debug;

public class BeeTask {

	private double step = 0;

	private List<BeeTask> childs = new ArrayList<BeeTask>();
	private BeeTask parent = null;
	private double all = 0;
	private boolean end = false;

	public BeeTask(int step) {
		this(step, null);
	}

	public BeeTask(int all, BeeTask parent) {
		this.all = all;
		this.parent = parent;
	}

	public BeeTask start(int all) {
		BeeTask child = new BeeTask(all, this);
		this.childs.add(child);
		return child;
	}

	public boolean isEnd() {
		return end;
	}

	public void next() {
		this.childs.clear();
		if (this.step < all) {
			this.step++;
		}
	}

	public BeeTask end() {
		this.end = true;
		this.step = this.all;

		if (this.parent == null) {
			return this;
		}
		return this.parent;
	}

	public void clearChild() {
		this.childs.clear();
	}

	public double getProgress() {
		if (all == 0) {
			return 0;
		}

		if (this.childs.size() > 1) {
			Debug.d();
		}

		double p = step / all;
		double childP = 0;
		for (BeeTask child : this.childs) {
			double c = child.getProgress();

			childP = childP + 1 / childs.size() * c;
		}
		p = p + 1 / all * childP;
		return p;
	}

	public BeeTask getMostTop() {
		if (this.parent != null) {
			return this.parent.getMostTop();
		} else {
			return this;
		}
	}
}
