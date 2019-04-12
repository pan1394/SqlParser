package com.linkstec.bee.core.codec.sql.sql;

public class SqlSubExists extends SqlNode {

	private SqlSelectNode select;

	private SqlFromNode from;

	private SqlWhereNode where;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EXISTS (");
		sb.append(select + " \n");
		sb.append(from + "\n");
		sb.append(where + ")\n");
		return sb.toString();
	}
}
