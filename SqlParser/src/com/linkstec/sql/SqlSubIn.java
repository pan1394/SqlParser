package com.linkstec.sql;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.sql.constants.SqlConstants;
import com.linkstec.utils.SqlUtilities;

public class SqlSubIn extends SqlNode{

	private SqlField field;
	
	private List<SqlField> items = new ArrayList<>();
	
	
	@Override
	protected void convert() {
		super.convert();
		String[] main =SqlUtilities.split(rawString, SqlConstants.REGEX_SPLIT_CHAR_IN);
		this.field = SqlNode.create(SqlField.class);
		this.field.setRawString(main[0]);
		String str = main[1].substring(1, main[1].length()-1);
		String[] its = SqlUtilities.split(str, SqlConstants.REGEX_SPLIT_CHAR_COMMA);
		for(String it : its) {
			SqlField e = SqlNode.create(SqlField.class);
			e.setRawString(it);
			items.add(e);
		}
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.field +"　IN  (");
		items.forEach( i -> sb.append(i.toString() + ","));
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}
}
