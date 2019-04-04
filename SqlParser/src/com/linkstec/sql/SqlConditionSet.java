package com.linkstec.sql;

import java.util.ArrayList;
import java.util.List;

public class SqlConditionSet extends SqlNode {
	
	private List<SqlCondition> set = new ArrayList<>();
	 
	public SqlConditionSet() {
	}
  
	public void add(SqlCondition c) {
		set.add(c);
	}
	
	public List<SqlCondition> getSet() {
		return set;
	}  
 
	@Override
	protected void convert() {
		 
			
		super.convert();
		 
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		if(conditionSet.isEmpty()) return "";
//		if(StringUtils.isNotBlank(conditionJoin)) {
//			sb.append("( \n");
//			conditionSet.forEach(c -> sb.append(c + "\n"));
//			sb.append(") \n");
//			sb.append(conditionJoin + "\n");
//		}else {
//			conditionSet.forEach(c -> sb.append(c + "\n"));
//		}
		return sb.toString();
	}
}
