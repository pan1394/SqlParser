package com.linkstec.bee.core.codec.sql.sql;

public class SqlCondition extends SqlNode {

	private SqlNode expression;

	private SqlCondition next;

	public SqlNode getExpression() {
		return expression;
	}

	public void setExpression(SqlNode expression) {
		this.expression = expression;
	}

	public SqlCondition getNext() {
		return next;
	}

	public void setNext(SqlCondition next) {
		this.next = next;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(expression);
		if (next != null) {
			sb.append("\n" + next.toString());
		}
		return sb.toString();
	}
}
