package com.linkstec.sql;

import org.apache.commons.lang3.StringUtils;

import com.linkstec.sql.constants.SQLConstants;

public class SqlSubQuery extends SqlNode{

	private String alias;
	
	private String selectString;
	
	private SqlNode selectTable;
	
	private SqlWhereNode where = SqlNode.create(SqlWhereNode.class);;
	
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSelectString() {
		return selectString;
	}

	public void setSelectString(String selectString) {
		this.selectString = selectString;
	}

	public SqlNode getSelectTable() {
		return selectTable;
	}

	public void setSelectTable(SqlNode selectTable) {
		this.selectTable = selectTable;
	}

	public SqlWhereNode getWhere() {
		return where;
	}

	public void setWhere(SqlWhereNode where) {
		this.where = where;
	}

	@Override
	protected void convert() {
		super.convert();
		if(StringUtils.contains(rawString, SQLConstants.REGEX_SELECT)){
			this.selectString = rawString;
		}else if(StringUtils.contains(rawString, SQLConstants.REGEX_FROM)) {
			String table = StringUtils.removeStart(rawString, SQLConstants.REGEX_FROM);
			this.selectTable = new SqlNode(StringUtils.trimToEmpty(table));
		}else {
			where.setRawString(rawString);
		} 
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("("); 
		sb.append("SELECT " + this.selectString + "\n");
		sb.append(String.format("FROM %s \n", this.selectTable));
		sb.append(this.where.toString() + "\n");
		sb.append(")");
		if(StringUtils.isNotBlank(alias))
			sb.append(" AS " + alias);
		return sb.toString();
	}
}
