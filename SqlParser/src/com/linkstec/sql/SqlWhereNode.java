package com.linkstec.sql;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.sql.constants.SqlConstants;
import com.linkstec.utils.SqlUtilities;

public class SqlWhereNode extends SqlNode{

	private List<SqlCondition> conditions = new ArrayList<>();
	private List<SqlSubExists> exists= new ArrayList<>();
	private List<SqlSubIn> inClause = new ArrayList<>();

	private boolean existsFlag;
	private boolean existsStart;
	private boolean existsEnd;
	
	private SqlSubExists subExists = null;

	public boolean isEmpty() {
		return exists.isEmpty() && conditions.isEmpty();
	}
    @Override
    protected void convert() {
    	super.convert();
    	//System.out.println(rawString);
    	
		if((!existsFlag && SqlUtilities.contains(SqlConstants.CONDITON_OPERATOR, rawString)) || (existsFlag && existsEnd)){
			SqlCondition c = SqlNode.create(SqlCondition.class);
			c.setRawString(rawString);
			conditions.add(c);
		}else if((!existsFlag && rawString.contains(SqlConstants.REGEX_SPLIT_CHAR_IN)) || (existsFlag && existsEnd)){
			SqlSubIn c = SqlNode.create(SqlSubIn.class);
			c.setRawString(rawString);
			inClause.add(c);
		}else if(!existsFlag && rawString.contains(SqlConstants.CONDITON_EXISTS)){
			existsFlag = true;
			subExists= SqlNode.create(SqlSubExists.class);
			exists.add(subExists);
		}else if(existsFlag && SqlUtilities.contains(SqlConstants.REGEX_START_PARENT, rawString)){
			existsStart = true;
		}else if(existsStart && !existsEnd && SqlUtilities.contains(SqlConstants.REGEX_END_PARENT, rawString)){
			existsEnd = true;
			existsFlag = false;
		}else if(existsStart && !existsEnd){
			subExists.setRawString(rawString);
			//System.out.println(rawString);
		}
		
 
    }
    
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WHERE \n");
		if(!conditions.isEmpty()){
			for(SqlCondition c  : conditions){
				sb.append(c .toString() + "\n");
			}
		}
		if(!inClause.isEmpty()){
			for(SqlSubIn c  : inClause){
				sb.append(c .toString() + "\n");
			}
		}
		if(!exists.isEmpty()){
			for(SqlSubExists c  : exists){
				sb.append(c .toString());
			}
		}
		if(sb.length() == 7) 
			return "";
		return sb.toString();
	}
}
