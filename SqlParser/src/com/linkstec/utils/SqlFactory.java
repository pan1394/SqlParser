package com.linkstec.utils;

import com.linkstec.sql.SqlCondition;
import com.linkstec.sql.SqlField;
import com.linkstec.sql.SqlJoinNode;
import com.linkstec.sql.SqlNode;

public class SqlFactory {

	public static SqlNode sqlNode(String string) {
		SqlNode node = new SqlNode(string);
		return node;
	}
	
	public static SqlField sqlField(String string) {
		SqlField node = SqlNode.create(SqlField.class);
		node.setRawString(string);
		return node;
	}
	
	public static SqlCondition sqlCondition(String string) {
		SqlCondition node = SqlNode.create(SqlCondition.class);
		node.setRawString(string);
		return node;
	}
	
	public static SqlJoinNode sqlJoinNode(String string) {
		SqlJoinNode node = SqlNode.create(SqlJoinNode.class);
		node.setRawString(string);
		return node;
	}
	
	
}
