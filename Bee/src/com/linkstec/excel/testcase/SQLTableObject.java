package com.linkstec.excel.testcase;

public class SQLTableObject {

	private String tableName;
	private String alias;

	public SQLTableObject(String tableName, String alias) {
		this.tableName = tableName;
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public String getTableName() {
		return tableName;
	}

	public boolean hasAlias(String alias) {
		return this.alias.equals(alias);
	}
}