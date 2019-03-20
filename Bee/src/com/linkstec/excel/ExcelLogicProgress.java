package com.linkstec.excel;

import java.util.List;

import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BLogicUnit;

public class ExcelLogicProgress {

	public final static int maxWith = 62;
	public final static int invoker_title_value_width = 18;
	private boolean doInvoker = true;

	private int row = 0;
	private int col = 0;
	private int width = maxWith;
	private Path path;

	private int method = 0;
	private int globalVar = 0;
	private int globalBlock = 0;

	private UnitPath unitPath;

	private List<BLogicUnit> currentUnits;
	private int currentUnitIndex;

	public ExcelLogicProgress() {
		path = new Path();
		path.setNumber(1);
	}

	public List<BLogicUnit> getCurrentUnits() {
		return currentUnits;
	}

	public void setCurrentUnits(List<BLogicUnit> currentUnits) {
		this.currentUnits = currentUnits;
	}

	public int getCurrentUnitIndex() {
		return currentUnitIndex;
	}

	public void setCurrentUnitIndex(int currentUnitIndex) {
		this.currentUnitIndex = currentUnitIndex;
	}

	public void decreaseDepth() {
		path = path.getParent();
		this.width = this.width + 1;
		this.col = this.col - 1;
		this.unitPath = new UnitPath(unitPath);
		row--;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void indent(int col) {
		this.width = this.width - col;
		this.col = this.col + col;
	}

	public void clearIndent(int col) {
		this.width = this.width + col;
		this.col = this.col - col;
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
		this.col = 7;
		method++;
		path = new Path();
		path.setNumber(method);
	}

	public void increaseGlobalBlock() {
		this.globalBlock++;
		path = new Path();
		path.setNumber(globalBlock);
		this.width = maxWith - 3;
		this.col = 7;
	}

	public void increaseGlobalVar() {
		this.globalVar++;

		path = new Path();
		path.setNumber(globalVar);
		this.width = maxWith - 3;
		this.col = 7;
	}

	public Path getPath() {
		return this.path;
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
		private String[] smalls = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
				"R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		private String[] marus = { "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪", "⑫", "⑬", "⑭", "⑮", "⑯", "⑰",
				"⑱", "⑲" };
		private String[] romas = { "ⅰ", "ⅱ", "ⅲ", "ⅳ", "ⅴ", "ⅵ", "ⅶ", "ⅷ", "ⅸ", "ⅹ" };

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
			int n = this.number - 1;
			int depth = this.getDepth();
			if (depth == 3) {
				return this.romas[n] + ")";
			} else if (depth == 4) {
				return this.number + ")";
			} else if (depth == 5) {
				return this.marus[n];
			} else if (depth == 6) {
				return "(" + this.smalls[n].toLowerCase() + ")";
			} else if (depth == 7) {
				return this.smalls[n].toLowerCase() + ")";
			} else if (depth == 8) {
				return this.romas[n] + ")";
			}
			return "・";
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
