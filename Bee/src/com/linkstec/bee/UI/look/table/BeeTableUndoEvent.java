package com.linkstec.bee.UI.look.table;

public class BeeTableUndoEvent {
	private Object value;
	private int row;
	private int column;

	public BeeTableUndoEvent(Object value, int row, int column) {
		this.value = value;
		this.row = row;
		this.column = column;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

}
