package com.linkstec.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SqlGroup extends SqlNode{
  
	private List<SqlField> items = new ArrayList<>();
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	@Override
	protected void convert() {
		super.convert();
		SqlField field = SqlNode.create(SqlField.class);
		if(StringUtils.isNotBlank(rawString)) {
			field.setRawString(rawString);
			items.add(field);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(items.size() > 0) {
			sb.append("GROUP BY \n(");
			items.forEach( f -> sb.append(f.toString() + ",")); 
			sb.setLength(sb.length() - 1);
			sb.append(")");
		}
		return sb.toString();
	}
}
