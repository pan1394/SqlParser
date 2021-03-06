package com.linkstec.bee.core.codec.sql.sql;

import java.util.List;

public class SqlSelectNode extends SqlNode {

	private List<SqlField> items;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		for (int i = 0; i < items.size(); i++) {
			if (i == 1) {
				sb.append(", ");
			}
			sb.append(items.get(i));
		}
		return sb.toString();
	}
}
