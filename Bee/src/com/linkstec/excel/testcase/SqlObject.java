package com.linkstec.excel.testcase;

import java.util.List;

public class SqlObject {
	private List<SqlUnit> units;
	private String name;

	public List<SqlUnit> getUnits() {
		return units;
	}

	public void setUnits(List<SqlUnit> units) {
		this.units = units;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
