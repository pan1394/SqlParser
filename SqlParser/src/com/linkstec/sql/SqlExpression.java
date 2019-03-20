package com.linkstec.sql;

public class SqlExpression extends SqlNode{

	private SqlField left;
	
	private SqlNode operator;
	
	private SqlNode right;
	
	public SqlExpression(SqlField left, SqlNode operator, SqlNode right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(left.toString()+" "+operator+ " "+ right.toString());
		return sb.toString();
	}
}
