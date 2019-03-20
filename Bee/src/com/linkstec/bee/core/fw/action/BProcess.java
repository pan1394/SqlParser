package com.linkstec.bee.core.fw.action;

public class BProcess {

	public static BProcessIF p;

	public static void check(Object node) {
		if (p != null)
			p.checkB(node);
	}

	public static void start(int steps) {
		if (p != null)
			p.startB(steps);
	}

	public static void go() {
		if (p != null)
			p.goB();
	}

	public static void end() {
		if (p != null)
			p.endB();
	}

}
