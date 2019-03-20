package com.linkstec.excel;

import java.util.List;

public class SqlUnit {
	private List<SqlRow> Fields;
	private List<SqlRow> tables;
	private List<SqlRow> where;
	private List<SqlRow> order;
	private List<SqlRow> group;

	public List<SqlRow> getFields() {
		return Fields;
	}

	public void setFields(List<SqlRow> Fields) {
		this.Fields = Fields;
	}

	public List<SqlRow> getTables() {
		return tables;
	}

	public void setTables(List<SqlRow> tables) {
		this.tables = tables;
	}

	public List<SqlRow> getWhere() {
		return where;
	}

	public void setWhere(List<SqlRow> where) {
		this.where = where;
	}

	public List<SqlRow> getOrder() {
		return order;
	}

	public void setOrder(List<SqlRow> order) {
		this.order = order;
	}

	public List<SqlRow> getGroup() {
		return group;
	}

	public void setGroup(List<SqlRow> group) {
		this.group = group;
	}
}
