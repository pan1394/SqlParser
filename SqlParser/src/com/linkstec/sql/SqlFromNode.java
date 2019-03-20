package com.linkstec.sql;

public class SqlFromNode extends SqlNode {

	private SqlNode table;

	@Override
	public String toString() {
		return "FROM " + this.table;
	}
}
