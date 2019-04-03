package com.linkstec.sql;

import org.apache.commons.lang3.StringUtils;

import com.linkstec.sql.constants.SQLConstants;
import com.linkstec.utils.Utilities;

public class SqlCondition extends SqlNode {

	private SqlField left;
	
	private SqlNode operator;
	
	private SqlField right;
	
	private String concatString;
	
	public SqlCondition() {
		
	}
	
	public SqlField getLeftNode() {
		return this.left;
	}
	
	public SqlField getRightNode() {
		return this.right;
	}
	
	public SqlCondition(SqlField left, SqlNode operator, SqlField right) {
		this.left = left;
		this.operator = operator;    
		this.right = right;
	}

	@Override
	protected void convert() {
		String partString = "";
		super.convert();
		if (Utilities.contains(SQLConstants.CONDITON_AND_OR, this.rawString)) { 
			String splitChar = Utilities.fetch(SQLConstants.CONDITON_AND_OR, rawString);
			//String[] main = Utilities.crop(rawString, splitChar);
			this.concatString = splitChar;
			partString = StringUtils.removeFirst(rawString, splitChar);
		}else{
			partString = rawString;
		}
		String op = Utilities.fetch(SQLConstants.CONDITON_OPERATOR, partString);
		this.operator = new SqlNode(op);
		String[] parts = Utilities.crop(partString, op);
		this.left = SqlNode.create(SqlField.class);
		this.right = SqlNode.create(SqlField.class);
		left.setRawString(parts[0]);
		right.setRawString(parts[1]);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(left.toString()+" "+operator+ " "+ right.toString());
		if(StringUtils.isNoneEmpty(this.concatString)) {
			sb.append(" " + this.concatString);
		}
		return sb.toString();
	}
}
