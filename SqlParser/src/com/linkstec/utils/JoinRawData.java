package com.linkstec.utils;

import java.util.ArrayList;
import java.util.List;

public class JoinRawData {

	private List<String> tableString = new ArrayList<>();
	
	private List<String> conditionString = new ArrayList<>();

	public List<String> getTableString() {
		return tableString;
	}

	public void setTableString(List<String> tableString) {
		this.tableString = tableString;
	}

	public List<String> getConditionString() {
		return conditionString;
	}

	public void setConditionString(List<String> conditionString) {
		this.conditionString = conditionString;
	}
	
	public void addTableString(String string) {
		tableString.add(SqlUtilities.tab2Space(string));
	}
	
	public void addConditionString(String string) {
		conditionString.add(SqlUtilities.tab2Space(string));
	}
	
	public void removeConditionString(String string) {
		conditionString.remove(SqlUtilities.tab2Space(string));
	}
}
