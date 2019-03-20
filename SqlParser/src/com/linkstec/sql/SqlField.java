package com.linkstec.sql;

public class SqlField extends SqlNode{

	private SqlTable owner;
	
	private SqlNode columnName;
	
	private String alias;
	
	public SqlField() {
		
	}
	
	public SqlField(SqlTable owner, SqlNode columnName, String alias ) { 
		this.owner = owner;
		this.columnName = columnName;
		this.alias = alias; 
	}
	
	public SqlTable getOwner() {
		return owner;
	}


	public void setOwner(SqlTable owner) {
		this.owner = owner;
	}


	public SqlNode getColumnName() {
		return columnName;
	}


	public void setColumnName(SqlNode columnName) {
		this.columnName = columnName;
	}


	public String getAlias() {
		return alias;
	}


	public void setAlias(String alias) {
		this.alias = alias;
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		if(this.owner != null && this.owner.toString().length()>0) {
			str.append(this.owner + ".");
		}
		str.append(this.columnName);
		if(this.alias != null && this.alias.trim().length() > 0 ) {
			str.append(" AS " + this.alias);
		}
		return String.format(str.toString());
	}
}
