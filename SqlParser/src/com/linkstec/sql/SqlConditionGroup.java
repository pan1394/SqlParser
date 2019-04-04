package com.linkstec.sql;

public class SqlConditionGroup extends SqlNode {

	private SqlNode node;
	 
	private String conditionJoin;
	
	public SqlConditionGroup() {
	
	}
  

	@Override
	protected void convert() {
		super.convert();
		//TODO
		//if rawString is "(", create a group;
		SqlConditionGroup group = new SqlConditionGroup();
		assign(node, group);
		// if rawString contains "="
		SqlConditionSet set = get(node);
		SqlCondition  c = new SqlCondition();
		c.setRawString(rawString);
		set.add(c);
		// if rawString is ")"
		 
	}
	
	
	private void assign(SqlNode node, SqlConditionGroup created) {
		if(node == null) {
			node = created;
		}else {
			SqlConditionGroup p = (SqlConditionGroup)node;
			assign(p.node, created);
		}
	}
	
	private SqlConditionSet get(SqlNode node) {
		SqlConditionSet set = null;
		if(node == null) {
			set = new SqlConditionSet();
			node = set;
		}else{
			if(node instanceof SqlConditionSet) {
				return (SqlConditionSet)node;
			}else {
				return get(node);
			}
		}
		return set;
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
