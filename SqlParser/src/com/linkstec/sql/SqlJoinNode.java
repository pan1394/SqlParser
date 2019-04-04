package com.linkstec.sql;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.parse.SqlObject;
import com.linkstec.sql.constants.JoinType;
import com.linkstec.sql.constants.SqlConstants;
import com.linkstec.utils.JoinRawData;
import com.linkstec.utils.SqlFactory;
import com.linkstec.utils.SqlUtilities;

public class SqlJoinNode extends SqlNode {

	public SqlJoinNode(String node) {
		super(node);
	}
	
	public SqlJoinNode() { 
	}
	
	private SqlNode table;
	private JoinType joinType;
	private List<SqlCondition> conditions = new ArrayList<>();
	
	public boolean isEmpty() {
		return conditions.isEmpty();
	}
	
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
		parse(rawString);
	}
	
	
	private void parse(String string) {
		if(SqlUtilities.contains(SqlConstants.CONDITON_OPERATOR, string)) { // condition object
			//System.out.println(string);
			SqlCondition con = new SqlCondition();
			con.setRawString(string);
			conditions.add(con);
		}else {
			//parse table and joinType
			if(getRawString().contains(SqlConstants.CONDITON_JOIN)) {
				String[] main = SqlUtilities.crop(string, SqlConstants.CONDITON_JOIN);
				this.table = new SqlNode(main[0]);
				this.joinType  = Enum.valueOf(JoinType.class, SqlUtilities.fetch(JoinType.getAll(), main[1])) ;
			}else {
				
			}
		}
	}
	
	public void convert(JoinRawData rd) {
		List<String> cs = rd.getConditionString();
		for(String c : cs) {
			conditions.add(SqlFactory.sqlCondition(c));
		}
		List<String> ts = rd.getTableString();
		int size = ts.size();
		if(size == 1) {
			String tblLine = ts.get(0);
			this.joinType = parseType(tblLine);
			this.table = SqlFactory.sqlNode(parseName(tblLine));
		}else {
			List<String> tbls = ts.subList(1, size - 1);
			SqlObject sqlObj = SqlObject.parse(tbls);
			String tblLine = ts.get(size -1);
			this.joinType = parseType(tblLine);
			sqlObj.getTable().setAlias(parseName(tblLine));
			this.table = sqlObj;
		}
	}
	
	private JoinType parseType(String line) {
		return Enum.valueOf(JoinType.class, SqlUtilities.fetch(JoinType.getAll(), line));
	}
	
	private String parseName(String line) {
		line = SqlUtilities.cutRight(line, SqlConstants.CONDITON_JOIN_SYBOL);
		line = SqlUtilities.cutLeft(line, SqlConstants.REGEX_END_PARENT);
		return line;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this.joinType  != null) {
			sb.append(String.format(" %s JOIN [* %s *] ON \n",  this.joinType, this.table));
		}
		for(SqlCondition c : this.conditions) {
			sb.append(c.toString() + "\n");
		}
		 return sb.toString();
	}

	
}
