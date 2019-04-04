package com.linkstec.sql;

import org.apache.commons.lang3.StringUtils;

import com.linkstec.sql.constants.SqlConstants;

public class SqlSubExists extends SqlNode{

	private String selectString;
	
	private SqlNode selectTable;
	
	private SqlWhereNode where = SqlNode.create(SqlWhereNode.class);;
	
	@Override
	protected void convert() {
		super.convert();
		if(StringUtils.contains(rawString, SqlConstants.REGEX_SELECT)){
			this.selectString = rawString;
		}else if(StringUtils.contains(rawString, SqlConstants.REGEX_FROM)) {
			String table = StringUtils.removeStart(rawString, SqlConstants.REGEX_FROM);
			this.selectTable = new SqlNode(StringUtils.trimToEmpty(table));
		}else {
			where.setRawString(rawString);
		} 
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EXISTS \n");
		sb.append("( \n");
		sb.append(selectString + " \n");
		sb.append(String.format("FROM %s \n", selectTable));
		if(where != null) {
			sb.append(where.toString());
		}
		sb.append(")");
		return sb.toString();
	}
}
