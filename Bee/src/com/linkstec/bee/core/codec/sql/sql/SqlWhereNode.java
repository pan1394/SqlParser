package com.linkstec.bee.core.codec.sql.sql;

public class SqlWhereNode extends SqlNode {

	private SqlCondition condition;

	@Override
	public String toString() {
		return "WHERE " + this.condition.toString();
	}
}
