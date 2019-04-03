package com.linkstec.sql;

import com.linkstec.utils.Utilities;

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
		if(clazz == null) return (T) new SqlNode();
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			 e.printStackTrace();
		}
		return (T) new SqlNode();
	}
	
	
	public SqlNode setRawStr(String rawString) {
		super.setRawString(rawString);
		return this;
	}
}
