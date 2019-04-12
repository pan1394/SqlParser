package com.linkstec.bee.core.codec.sql.sql;

public class SqlNode {

	private String node;

	public SqlNode() {

	}

	public SqlNode(String node) {
		this.node = node;
	}

	@Override
	public String toString() {
		return this.node;
	}
}
