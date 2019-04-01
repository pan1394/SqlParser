package com.linkstec.sql;

import com.linkstec.sql.constants.SQLConstants;
import com.linkstec.utils.Utlities;

public class SqlCondition extends SqlNode {

	private SqlField left;
	
	private SqlNode operator;
	
	private SqlNode right;
	
	public SqlCondition() {
		
	}
	
	public SqlCondition(SqlField left, SqlNode operator, SqlNode right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	protected void convert() {
		super.convert();
		String op = Utlities.fetch(SQLConstants.CONDITON_OPERATOR, rawString);
		this.operator = new SqlNode(op);
		String[] parts = Utlities.crop(rawString, op);
		this.left = SqlNode.create(SqlField.class);
		this.right = SqlNode.create(SqlField.class);
		left.setRawString(parts[0]);
		right.setRawString(parts[1]);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(left.toString()+" "+operator+ " "+ right.toString());
		return sb.toString();
	}
}
