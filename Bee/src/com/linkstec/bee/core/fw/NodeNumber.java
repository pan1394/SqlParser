package com.linkstec.bee.core.fw;

import java.io.Serializable;

public class NodeNumber implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7885198370555219803L;

	private int number = 0;
	private IUnit parent;
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void increaseNumber() {
		this.number++;
	}

	public void decreaseNumber() {
		this.number--;
	}

	public Object getParent() {
		return parent;
	}

	public void setParent(IUnit parent) {
		this.parent = parent;
	}

	public String toString() {

		String s = this.getString();
		s = s + ". ";
		return s;
	}

	public String getString() {
		String s = "" + number;
		if (number == 0) {
			s = "";
		}
		if (title != null) {
			s = title;
		}
		if (this.parent != null) {
			if (this.parent.getNumber() != null) {
				if (title == null) {
					int depth = this.getDepth();
					if (depth == 2) {
						s = "(" + s + ")";
					} else if (depth == 3) {
						s = "<" + s + ">";
					} else if (depth == 4) {
						s = "(" + s + ")";
					} else if (depth == 5) {
						s = s + ")";
					} else {
						s = parent.getNumber().getString() + "-" + s;
					}
				} else {
					s = parent.getNumber().getString() + "-" + s;
				}
			}
		}
		return s;
	}

	public int getDepth() {
		if (this.parent == null) {
			return 0;
		} else {
			if (this.parent.getNumber() != null) {
				return this.parent.getNumber().getDepth() + 1;
			} else {
				return 0;
			}
		}
	}

	public String getFullTitle() {
		if (this.title == null) {
			return null;
		}
		String s = this.title;

		if (this.parent != null) {
			if (this.parent.getNumber() != null) {
				String title = parent.getNumber().getFullTitle();
				if (title != null)
					s = title + "-" + s;
			}
		}
		return s;
	}

}
