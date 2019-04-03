package com.linkstec.sql;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.sql.constants.JoinType;
import com.linkstec.sql.constants.SQLConstants;
import com.linkstec.utils.Utilities;

public class SqlJoinNode extends SqlNode {

	public SqlJoinNode(String node) {
		super(node);
	}
	
	public SqlJoinNode() { 
	}
	
	private SqlNode table;
	private JoinType joinType;
	private List<SqlCondition> conditions = new ArrayList<>();
	
	public SqlNode getTable() {
		return table;
	}

	public void setTable(SqlNode table) {
		this.table = table;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

	public List<SqlCondition> getConditions() {
		return conditions;
	}

	public void setConditions(List<SqlCondition> conditions) {
		this.conditions = conditions;
	}

	
	@Override
	protected void convert() {
		super.convert();
		if(Utilities.contains(SQLConstants.CONDITON_OPERATOR, getRawString())) { // condition object
			//System.out.println(rawString);
			SqlCondition con = new SqlCondition();
			con.setRawString(rawString);
			conditions.add(con);
		}else {
			//parse table and joinType
			if(getRawString().contains(SQLConstants.CONDITON_JOIN)) {
				String[] main = Utilities.crop(rawString, SQLConstants.CONDITON_JOIN);
				this.table = new SqlNode(main[0]);
				this.joinType  = Enum.valueOf(JoinType.class, Utilities.fetch(JoinType.getAll(), main[1])) ;
			}else {
				
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this.joinType  != null) {
			sb.append(this.joinType.toString() + " JOIN ON \n");
		}
		for(SqlCondition c : this.conditions) {
			sb.append(c.toString() + "\n");
		}
		 return sb.toString();
	}
}
