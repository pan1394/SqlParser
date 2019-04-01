package com.linkstec.sql;

import org.apache.commons.lang3.StringUtils;

import com.linkstec.sql.constants.SQLConstants;
import com.linkstec.utils.Utlities;

public class SqlTable extends SqlNode {

	private String tableName;

	private String alias;

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	protected void convert() {
		super.convert();
		if (this.rawString.contains(SQLConstants.REGEX_SPLIT_CHAR_AS)) { // has alias
			String[] main = Utlities.crop(this.rawString, SQLConstants.REGEX_SPLIT_CHAR_AS);
			this.alias = StringUtils.trimToEmpty(main[1]);
			this.tableName = StringUtils.trimToEmpty(main[0]);
		} else {
			this.alias = "";
			this.tableName = this.rawString;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.tableName);
		if (StringUtils.isNotEmpty(this.alias))
			sb.append(SQLConstants.REGEX_SPLIT_CHAR_AS + this.alias);
		return sb.toString();
	}
}
