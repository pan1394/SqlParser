package com.linkstec.sql;

public class SqlSubFrom extends SqlNode{

	private String alias;
	
	private SqlSelectNode select;
	
	private SqlFromNode from;
	
	private SqlWhereNode where;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FROM (");
		sb.append(select + " \n");
		sb.append(from + "\n");
		sb.append(where + ")");
		if(alias != null && alias.length()>0)
			sb.append(" AS " + alias);
		sb.append("\n");
		return sb.toString();
	}
}
