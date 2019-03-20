package com.linkstec.bee.core.codec.excel;

import org.apache.poi.ss.usermodel.Sheet;

import com.linkstec.bee.core.fw.IUnit;

public class ExcelLogicProgress {

	public final static int maxWith = 48;
	public final static int invoker_title_value_width = 12;
	private boolean doInvoker = true;

	private int row = 0;
	private int col = 0;
	private int width = maxWith;
	private Path path;

	private int method = 0;
	private int globalVar = 0;
	private int globalBlock = 0;

	private UnitPath unitPath;
	private Sheet sheet;
	private ExcelStyles style;

	public ExcelLogicProgress(Sheet sheet, ExcelStyles style) {
		this.sheet = sheet;
		this.style = style;
		path = new Path();
		path.setNumber(1);
	}

	public void decreaseDepth() {
		path = path.getParent();
		this.width = this.width + 1;
		this.col = this.col - 1;
		this.unitPath = new UnitPath(unitPath);
		row--;
	}

	public void increaseDepth() {
		Path parent = path;
		path = new Path();
		path.setParent(parent);
		path.setNumber(0);
		this.width = this.width - 1;
		this.col = this.col + 1;
		this.unitPath = new UnitPath(unitPath);

	}

	public void increaseUnit(IUnit unit) {
		path.setNumber(path.getNumber() + 1);
		UnitPath old = this.unitPath;
		if (this.unitPath == null) {
			this.unitPath = new UnitPath();
		}
		this.unitPath.setParent(old);

	}

	public boolean isDoInvoker() {
		return doInvoker;
	}

	public void setDoInvoker(boolean doInvoker) {
		this.doInvoker = doInvoker;
	}

	public void decreaseUnit() {
		path.setNumber(path.getNumber() - 1);
	}

	public UnitPath getUnitPath() {
		return this.unitPath;
	}

	public void increaseMethod() {

		this.width = maxWith - 3;
		this.col = 2;
		method++;

		path = new Path();
		path.setNumber(method);
	}

	public void increaseGlobalBlock() {
		this.globalBlock++;
		path = new Path();
		path.setNumber(globalBlock);
		this.width = maxWith - 3;
		this.col = 2;
	}

	public void increaseGlobalVar() {
		this.globalVar++;

		path = new Path();
		path.setNumber(globalVar);
		this.width = maxWith - 3;
		this.col = 2;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public int getWidth() {
		return width;
	}

	public int getMethod() {
		return method;
	}

	public int getGlobalVar() {
		return globalVar;
	}

	public int getGlobalBlock() {
		return globalBlock;
	}

	public static class Path {
		private int number;
		private Path parent;

		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}

		public Path getParent() {
			return parent;
		}

		public void setParent(Path parent) {
			this.parent = parent;
		}

		public String toString() {
			String s = "";
			if (this.getParent() != null) {
				s = this.getParent().toString() + "-" + this.number;
			} else {
				s = "" + this.number;
			}
			return s;
		}

		public int getDepth() {
			if (this.getParent() != null) {
				return 1 + this.getParent().getDepth();
			}
			return 1;
		}

	}

	public static class UnitPath {
		private UnitPath parent;
		private int assignNumber = 0;
		private boolean continuous = false;

		public UnitPath() {

		}

		public UnitPath(UnitPath parent) {
			this.parent = parent;
		}

		public UnitPath getParent() {
			return parent;
		}

		public void setParent(UnitPath parent) {
			this.parent = parent;
		}

		public boolean isContinuous() {
			return continuous;
		}

		public void setContinuous(boolean continuous) {
			this.continuous = continuous;
		}

		public int getAssignNumber() {
			return assignNumber;
		}

		public void setAssignNumber(int assignNumber) {
			this.assignNumber = assignNumber;
		}

	}

}
