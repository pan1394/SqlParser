package com.linkstec.sql;

import org.apache.commons.lang3.StringUtils;

import com.linkstec.sql.constants.SqlConstants;
import com.linkstec.utils.SqlUtilities;

public class SqlTable extends SqlNode {

	private SqlNode table;

	private String alias;

	public SqlNode getTable() {
		return table;
	}

	public void setTable(SqlNode table) {
		this.table = table;
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
		if (this.rawString.contains(SqlConstants.REGEX_SPLIT_CHAR_AS)) { // has alias
			String[] main = SqlUtilities.crop(this.rawString, SqlConstants.REGEX_SPLIT_CHAR_AS);
			this.alias = StringUtils.trimToEmpty(main[1]);
			this.table =new SqlNode(StringUtils.trimToEmpty(main[0]));
		} else {
			this.alias = "";
			this.table = new SqlNode(this.rawString);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.table);
		if (StringUtils.isNotEmpty(this.alias))
			sb.append(SqlConstants.REGEX_SPLIT_CHAR_AS + this.alias);
		return sb.toString();
	}
}
