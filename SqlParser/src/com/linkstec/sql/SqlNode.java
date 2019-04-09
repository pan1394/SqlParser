package com.linkstec.sql;

import com.linkstec.utils.SqlUtilities;

public class SqlNode extends AbstractNode{
 
	
	public SqlNode() {
	}
	
	public SqlNode(String node) {
		this.rawString = SqlUtilities.leftTrimAllBlank( node);
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
}
