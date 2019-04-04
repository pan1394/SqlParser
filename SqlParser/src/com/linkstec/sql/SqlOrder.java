package com.linkstec.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.linkstec.sql.constants.SqlConstants;
import com.linkstec.utils.SqlUtilities;

public class SqlOrder extends SqlNode{
  
	private List<SqlField> items = new ArrayList<>();
	
	public List<SqlField> getItems() {
		return items;
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	private String sort;
	
	@Override
	protected void convert() {
		super.convert();
		if(StringUtils.contains(rawString, SqlConstants.REGEX_ORDER_BY)) {
			if(SqlUtilities.contains(SqlConstants.ORDER_VALE, rawString)) {
				this.sort = SqlUtilities.fetch(SqlConstants.ORDER_VALE, rawString);
				this.rawString = StringUtils.removeEnd(rawString, this.sort);
			}
			String[] all = SqlUtilities.split(rawString, SqlConstants.REGEX_SPLIT_CHAR_COMMA);
			for(String f: all) {
				SqlField field = SqlField.create(SqlField.class);
				field.setRawString(f);
				items.add(field);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(items.isEmpty()) {
			return "";
		}
		items.forEach(i -> sb.append(i + ","));
		sb.setLength(sb.length() - 1);
		if(StringUtils.isNotBlank(sort)) {
			sb.append(" " + this.sort);
		}
		return sb.toString();
	}
}
