package com.linkstec.excel.testcase;

public enum CaseColumn {

	NO(1), SERVICE_ID(3), BIG_CATEGORY(4), MID_CATEGORY(5), LITLE_CATEGORY(6), CASE(7), EXPECTED_RESULT(8);

	private int column;

	private CaseColumn(int column) {
		this.column = column;
	}

	public int getColumn() {
		return this.column;
	}
}
