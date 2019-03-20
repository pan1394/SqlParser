package com.linkstec.sql;

public class SqlJointNode extends SqlNode {

	public SqlJointNode(String node) {
		super(node);
	}
	
	public SqlJointNode(String node, SqlNode next) {
		super(node);
		this.next = next;
	}
	
	private SqlNode next;
	
	@Override
	public String toString() {
		if(next == null)
			return super.toString();
		else
			return super.toString() + " "+ this.next.toString();
	}
}
