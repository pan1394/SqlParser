package com.linkstec.excel.testcase;

public enum CaseSQLColumn {

	NO(1), BIG_CATEGORY(3), MID_CATEGORY(4), LITLE_CATEGORY(5), CASE(6), EXPECTED_RESULT(7);

	private int column;

	private CaseSQLColumn(int column) {
		this.column = column;
	}

	public int getColumn() {
		return this.column;
	}
}
