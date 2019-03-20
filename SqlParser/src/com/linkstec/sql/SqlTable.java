package com.linkstec.sql;

public class SqlTable extends SqlNode{

	private String tableName;
	
	private String alias;
 
	public SqlTable(String tableName, String alias) {
		super(tableName);
		this.tableName = tableName;
		this.alias = alias;
	}
	
	public SqlTable(String tableName) {
		this(tableName, null);
	}
 
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Override
	public String toString() {
		return this.tableName;
	}
}
