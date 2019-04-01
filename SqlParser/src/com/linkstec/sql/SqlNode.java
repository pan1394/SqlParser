package com.linkstec.sql;

public class SqlNode extends AbstractNode{
 
	
	public SqlNode() {
	}
	
	public SqlNode(String node) {
		this.rawString = node;
	}
	
	@Override
	public String toString() {
		return this.rawString;
	}

	@Override
	protected void convert() {
		 
	}
	
	public static <T extends SqlNode> T create(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			 e.printStackTrace();
		}
		return (T) new SqlNode();
	}
}
